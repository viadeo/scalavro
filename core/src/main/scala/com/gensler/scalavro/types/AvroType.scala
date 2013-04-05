package com.gensler.scalavro
package types

import com.gensler.scalavro
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.error._
import com.gensler.scalavro.JsonSchemaProtocol._

import scala.util.{Try, Success, Failure}
import scala.language.existentials
import scala.reflect.api.{ Universe, Mirror, TypeCreator }
import scala.reflect.runtime.universe._

import spray.json._

import java.io.DataOutputStream

abstract class AvroType[T: TypeTag] extends JsonSchemifiable with CanonicalForm {

  final val tag: TypeTag[T] = typeTag[T]

  /**
    * The corresponding Scala type for this Avro type.
    */
  type scalaType = T

  /**
    * Returns the Avro type name for this schema.
    */
  def typeName(): String

  /**
    * Returns true if this represents a primitive Avro type.
    */
  def isPrimitive(): Boolean

  /**
    * Returns the JSON representation of this Avro type schema.
    */
  def schema(): spray.json.JsValue

  /**
    * Returns the fully self-describing JSON representation of this Avro type
    * schema.
    */
  def selfContainedSchema(
    resolvedSymbols: scala.collection.mutable.Set[String] = scala.collection.mutable.Set[String]()
  ): spray.json.JsValue

  /**
    * Returns the schema name if this is an instance of [[AvroNamedType]], or
    * the expanded schema otherwise.
    */
  def schemaOrName(): spray.json.JsValue =
    if (this.isInstanceOf[AvroRecord[_]] ||
        this.isInstanceOf[AvroError[_]] ||
        this.isInstanceOf[AvroEnum[_]] ||
        this.isInstanceOf[AvroFixed[_]]
    ) {
      this.asInstanceOf[AvroNamedType[_]].name.toJson
    }
    else this.schema

  /**
    * == Internal API ==
    * 
    * Returns the fully qualified schema name if this is an instance of
    * [[AvroNamedType]], or the parsing canonical form of this type schema
    * otherwise.
    */
  private[scalavro] def canonicalFormOrFullyQualifiedName(): spray.json.JsValue =
    if (this.isInstanceOf[AvroRecord[_]] ||
        this.isInstanceOf[AvroError[_]] ||
        this.isInstanceOf[AvroEnum[_]] ||
        this.isInstanceOf[AvroFixed[_]]
    ) {
      this.asInstanceOf[AvroNamedType[_]].fullyQualifiedName.toJson
    }
    else this.parsingCanonicalForm

  /**
    * Returns the JSON schema for this type in "parsing canonical form".
    *
    * _X_ [PRIMITIVES] Convert primitive schemas to their simple form (e.g.,
    *     int instead of {"type":"int"}).
    *
    * _X_ [FULLNAMES] Replace short names with fullnames, using applicable
    *     namespaces to do so. Then eliminate namespace attributes, which are
    *     now redundant.
    *
    * _X_ [STRIP] Keep only attributes that are relevant to parsing data, which
    *     are: type, name, fields, symbols, items, values, size. Strip all
    *     others (e.g., doc and aliases).
    *
    * _X_ [ORDER] Order the appearance of fields of JSON objects as follows:
    *     name, type, fields, symbols, items, values, size. For example, if an
    *     object has type, name, and size fields, then the name field should
    *     appear first, followed by the type and then the size fields.
    *
    * _X_ [INTEGERS] Eliminate quotes around and any leading zeros in front of
    *     JSON integer literals (which appear in the size attributes of fixed
    *     schemas).
    *
    * _X_ [WHITESPACE] Eliminate all whitespace in JSON outside of string
    *     literals.
    */
  def parsingCanonicalForm(): JsValue = schema

  /**
    * _X_ [STRINGS] For all JSON string literals in the schema text, replace
    *     any escaped characters (e.g., \\uXXXX escapes) with their UTF-8
    *     equivalents.
    */
  def writeCanonicalForm(os: java.io.OutputStream) {
    new DataOutputStream(os) writeUTF parsingCanonicalForm.toString
  }

  override def toString(): String = {
    val className = getClass.getSimpleName
    if (className endsWith "$") className.dropRight(1) else className
  }

  /**
    * Returns true if this type depends upon the supplied type.
    */
  def dependsOn(thatType: AvroType[_]): Boolean

}

object AvroType {

  import com.gensler.scalavro.types.primitive._
  import com.gensler.scalavro.types.complex._
  import com.gensler.scalavro.util.ReflectionHelpers
  import scala.collection.immutable.ListMap
  import java.util.concurrent.atomic.AtomicReference

  // primitive type cache table
  private val primitiveTypeCache: ListMap[Type, AvroType[_]] = ListMap(
    typeOf[Unit]      -> AvroNull,
    typeOf[Boolean]   -> AvroBoolean,
    typeOf[Seq[Byte]] -> AvroBytes,
    typeOf[Double]    -> AvroDouble,
    typeOf[Float]     -> AvroFloat,
    typeOf[Int]       -> AvroInt,
    typeOf[Long]      -> AvroLong,
    typeOf[String]    -> AvroString
  )

  // complex type cache table, initially empty
  private[scalavro] val complexTypeCache =
    new AtomicReference[ListMap[Type, AvroType[_]]](
      ListMap[Type, AvroType[_]]()
    )

  /**
    * Returns a `Success[AvroType[T]]` if an analogous AvroType is available
    * for the supplied type.
    */
  def fromType[T](implicit typeTag: TypeTag[T]): Try[AvroType[T]] = fromTypeHelper(typeTag)

  private def fromTypeHelper[T](
    implicit tt: TypeTag[T],
    processedTypes: Set[Type] = Set[Type]()
  ): Try[AvroType[T]] = Try {

    if (processedTypes exists { _ =:= tt.tpe }) throw new CyclicTypeDependencyException(
      "A cyclic type dependency was detected while attempting to " +
      "synthesize an AvroType for  type [%s]" format tt.tpe
    )

    val avroType = primitiveTypeCache.collectFirst { case (tpe, at) if tpe =:= tt.tpe => at } match {

      // primitive type cache hit
      case Some(primitive) => primitive

      // primitive type cache miss
      case None => complexTypeCache.get.collectFirst { case (tpe, at) if tpe =:= tt.tpe => at } match {

        // complex type cache hit
        case Some(complex) => complex

        // cache miss
        case None => {

          val newComplexType = {
            // sequences
            if (tt.tpe.typeConstructor =:= typeOf[Seq[_]].typeConstructor) tt.tpe match {
              case TypeRef(_, _, List(itemType)) => new AvroArray()(ReflectionHelpers.tagForType(itemType))
            }

            // string-keyed maps
            else if (tt.tpe <:< typeOf[Map[String, _]]) tt.tpe match {
              case TypeRef(_, _, List(stringType, itemType)) => new AvroMap()(ReflectionHelpers.tagForType(itemType))
            }

            // binary disjunctive unions
            else if (tt.tpe <:< typeOf[Either[_, _]]) tt.tpe match {
              case TypeRef(_, _, List(left, right)) => new AvroUnion()(
                ReflectionHelpers.tagForType(left),
                ReflectionHelpers.tagForType(right)
              )
            }

            // case classes
            else if (tt.tpe <:< typeOf[Product] && tt.tpe.typeSymbol.asClass.isCaseClass) {
              tt.tpe match { case TypeRef(prefix, symbol, _) =>
                new AvroRecord[T](
                  name      = symbol.name.toString,
                  fields    = ReflectionHelpers.caseClassParamsOf[T].toSeq map { case (name, tag) => {
                    val fieldType = fromTypeHelper(tag, (processedTypes + tt.tpe)).get
                    AvroRecord.Field(name, fieldType)
                  }},
                  namespace = Some(prefix.toString.stripSuffix(".type"))
                )
              }
            }

            // other types are not handled
            else throw new IllegalArgumentException(
              "Unable to find or make an AvroType for the supplied type [%s]" format tt.tpe
            )
          }

          // add the synthesized AvroType to the complex type cache table
          var tries = 0
          var cacheUpdated = false
          while (! cacheUpdated && tries < 3) {
            val currentCache = complexTypeCache.get
            cacheUpdated = complexTypeCache.compareAndSet(
              currentCache,
              currentCache + (tt.tpe -> newComplexType)
            )
            tries += 1
          }

          newComplexType
        }
      }
    }

    avroType.asInstanceOf[AvroType[T]]
  }

}