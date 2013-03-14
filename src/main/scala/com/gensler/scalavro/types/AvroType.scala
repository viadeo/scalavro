package com.gensler.scalavro
package types

import com.gensler.scalavro
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}
import com.gensler.scalavro.JsonSchemaProtocol._

import scala.util.{Try, Success, Failure}
import scala.language.existentials
import scala.reflect.api.{ Universe, Mirror, TypeCreator }
import scala.reflect.runtime.universe._

import spray.json._

trait AvroType[T] extends JsonSchemifiable {

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
  def fromType[T](implicit tt: TypeTag[T]): Try[AvroType[T]] = Try {

    val avroType = primitiveTypeCache.collectFirst { case (tag, at) if tt.tpe =:= tag.tpe => at } match {

      // primitive type cache hit
      case Some(primitive) => primitive

      case None => complexTypeCache.collectFirst { case (tag, at) if tt.tpe =:= tag.tpe => at } match {

        // complex type cache hit
        case Some(complex) => complex

        // cache miss
        case None => {

          val newComplexType = {
            // lists, sequences, etc
            if (tt.tpe <:< typeOf[Seq[_]]) tt.tpe match {
              case TypeRef(_, _, List(itemType)) => fromSeqType(ruTagFor(itemType))
            }

            // string-keyed maps
            else if (tt.tpe <:< typeOf[Map[String, _]]) tt.tpe match {
              case TypeRef(_, _, List(stringType, itemType)) => fromMapType(ruTagFor(itemType))
            }

            // binary disjunctive unions
            else if (tt.tpe <:< typeOf[Either[_, _]]) tt.tpe match {
              case TypeRef(_, _, List(left, right)) => fromEitherType(ruTagFor(left), ruTagFor(right))
            }

            // case classes
            else if (tt.tpe <:< typeOf[Product] && tt.tpe.typeSymbol.asClass.isCaseClass) {
              tt.tpe match { case TypeRef(prefix, symbol, _) =>
                new AvroRecord(
                  name      = symbol.name.toString,
                  namespace = prefix.toString.stripSuffix(".type"),
                  fields    = formalConstructorParamsOf[T].toSeq map { case (name, tag) =>
                    AvroRecord.Field(name, AvroType.fromType(tag).get)
                  }
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
    constructorMethodSymbol.paramss(0).map { sym =>
      sym.name.toString -> ruTagFor(tt.tpe.member(sym.name).asMethod.returnType)
    }.toMap
  }

  private[scalavro] def ruTagFor(tpe: Type): TypeTag[_] = TypeTag(
    classLoaderMirror,
    new TypeCreator {
      def apply[U <: Universe with Singleton](m: Mirror[U]) = tpe.asInstanceOf[U#Type]
    }
  )

  private def fromSeqType[A](itemType: TypeTag[_ <: A]) = 
    new AvroArray()(itemType)

  private def fromMapType[A](itemType: TypeTag[_ <: A]) =
    new AvroMap()(itemType)

  private def fromEitherType[A, B](left: TypeTag[_ <: A], right: TypeTag[_ <: B]) =
    new AvroUnion()(left, right)

}