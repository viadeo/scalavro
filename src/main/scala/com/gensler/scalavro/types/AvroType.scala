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

trait AvroType[T] extends JsonSchemifiable with CanonicalForm {

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
    * Returns a serialized representation of the supplied object.  Throws a
    * SerializationException if writing is unsuccessful. 
    */
  @throws[AvroSerializationException[_]]
  def write(obj: T): Seq[Byte]

  /**
    * Attempts to create an object of type T by reading the required data from
    * the supplied bytes.
    */
  def read(bytes: Seq[Byte]): Try[T]

  /**
    * Returns the canonical JSON representation of this Avro type.
    */
  def schema(): spray.json.JsValue = typeName.toJson

  /**
    * Returns the schema name if this is an instance of [[AvroNamedType]], or
    * the expanded schema otherwise.
    */
  def schemaOrName(): spray.json.JsValue =
    if (this.isInstanceOf[AvroRecord[_]] ||
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
        this.isInstanceOf[AvroEnum[_]] ||
        this.isInstanceOf[AvroFixed[_]]
    ) {
      this.asInstanceOf[AvroNamedType[_]].fullyQualifiedName.toJson
    }
    else this.parsingCanonicalForm

  /**
    * == Internal API ==
    *
    * Returns the schema name if this is an instance of [[AvroNamedType]], or
    * the canonical JSON representation of the supplied Avro type, or
    * the JSON representation of [[AvroNull]] if no corresponding AvroType
    * can be found for the supplied type.
    */
  private[scalavro] def typeSchemaOrNull[A: TypeTag] =
    AvroType.fromType[A] match {
      case Success(avroType) => avroType.schemaOrName
      case Failure(_) => com.gensler.scalavro.types.primitive.AvroNull.schema
    }

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
    * ___ [STRINGS] For all JSON string literals in the schema text, replace
    *     any escaped characters (e.g., \\uXXXX escapes) with their UTF-8
    *     equivalents.
    *
    * _X_ [INTEGERS] Eliminate quotes around and any leading zeros in front of
    *     JSON integer literals (which appear in the size attributes of fixed
    *     schemas).
    *
    * _X_ [WHITESPACE] Eliminate all whitespace in JSON outside of string
    *     literals.
    */
  def parsingCanonicalForm(): JsValue = schema

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

  val classLoaderMirror = runtimeMirror(getClass.getClassLoader)

  // primitive type cache table
  private val primitiveTypeCache: Map[TypeTag[_], AvroType[_]] = Map(
    typeTag[Unit]      -> AvroNull,
    typeTag[Boolean]   -> AvroBoolean,
    typeTag[Seq[Byte]] -> AvroBytes,
    typeTag[Double]    -> AvroDouble,
    typeTag[Float]     -> AvroFloat,
    typeTag[Int]       -> AvroInt,
    typeTag[Long]      -> AvroLong,
    typeTag[String]    -> AvroString
  )

  // complex type cache table, initially empty
  private[scalavro] var complexTypeCache = Map[TypeTag[_], AvroType[_]]()

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

    val avroType = primitiveTypeCache.collectFirst { case (tag, at) if tt.tpe =:= tag.tpe => at } match {

      // primitive type cache hit
      case Some(primitive) => primitive

      // primitive type cache miss
      case None => complexTypeCache.collectFirst { case (tag, at) if tt.tpe =:= tag.tpe => at } match {

        // complex type cache hit
        case Some(complex) => complex

        // cache miss
        case None => {

          val newComplexType = {
            // lists, sequences, etc
            if (tt.tpe <:< typeOf[Seq[_]]) tt.tpe match {
              case TypeRef(_, _, List(itemType)) => new AvroArray()(tagForType(itemType))
            }

            // string-keyed maps
            else if (tt.tpe <:< typeOf[Map[String, _]]) tt.tpe match {
              case TypeRef(_, _, List(stringType, itemType)) => new AvroMap()(tagForType(itemType))
            }

            // binary disjunctive unions
            else if (tt.tpe <:< typeOf[Either[_, _]]) tt.tpe match {
              case TypeRef(_, _, List(left, right)) => new AvroUnion()(tagForType(left), tagForType(right))
            }

            // case classes
            else if (tt.tpe <:< typeOf[Product] && tt.tpe.typeSymbol.asClass.isCaseClass) {
              tt.tpe match { case TypeRef(prefix, symbol, _) =>
                new AvroRecord(
                  name      = symbol.name.toString,
                  fields    = formalConstructorParamsOf[T].toSeq map { case (name, tag) =>
                    AvroRecord.Field(name, fromTypeHelper(tag, (processedTypes + tt.tpe)).get)
                  },
                  namespace = Some(prefix.toString.stripSuffix(".type"))
                )
              }
            }

            // other types are not handled
            else throw new IllegalArgumentException(
              "Unable to find or make an AvroType for the supplied type []" format tt.tpe
            )
          }

          // add the synthesized AvroType to the complex type cache table
          complexTypeCache += tt -> newComplexType

          newComplexType
        }
      }
    }

    avroType.asInstanceOf[AvroType[T]]
  }


  private def formalConstructorParamsOf[T: TypeTag]: Map[String, TypeTag[_]] = {
    val tt: TypeTag[T] = typeTag[T]
    val classSymbol = tt.tpe.typeSymbol.asClass
    val classMirror = classLoaderMirror reflectClass classSymbol
    val constructorMethodSymbol = tt.tpe.declaration(nme.CONSTRUCTOR).asMethod
    constructorMethodSymbol.paramss.reduceLeft( _ ++ _ ).map { sym =>
      sym.name.toString -> tagForType(tt.tpe.member(sym.name).asMethod.returnType)
    }.toMap
  }

  private[scalavro] def tagForType(tpe: Type): TypeTag[_] = TypeTag(
    classLoaderMirror,
    new TypeCreator {
      def apply[U <: Universe with Singleton](m: Mirror[U]) = tpe.asInstanceOf[U#Type]
    }
  )

}