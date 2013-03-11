package com.gensler.scalavro.types

import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure}

import spray.json._

trait AvroType[T] extends DefaultJsonProtocol {

  /**
    * The corresponding Scala type for this Avro type.
    */
  type scalaType = T

  /**
    * Returns the Avro type name for this schema.
    */
  def typeName(): String

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

}

object AvroType {

  import com.gensler.scalavro.types.primitive._
  import com.gensler.scalavro.types.complex._

  private val primitiveTags: Map[TypeTag[_], AvroType[_]] = Map(
    typeTag[Unit]      -> AvroNull,
    typeTag[Boolean]   -> AvroBoolean,
    typeTag[Seq[Byte]] -> AvroBytes,
    typeTag[Double]    -> AvroDouble,
    typeTag[Float]     -> AvroFloat,
    typeTag[Int]       -> AvroInt,
    typeTag[Long]      -> AvroLong,
    typeTag[String]    -> AvroString
  )

  // TODO: also cache complex avro types for performance

  /**
    * Returns a `Success[AvroType[T]]` if an analogous AvroType is available
    * for the supplied type.
    */
  def fromTypeTag[T](implicit tt: TypeTag[T]): Try[AvroType[T]] = Try {
    import scala.reflect.api._

    val avroType = primitiveTags.get(tt) match {
      case Some(primitive) => primitive
      case None            => {
        if (tt.tpe <:< typeOf[Seq[_]]) {
          tt.tpe match {
            case TypeRef(_, _, List(itemType)) => {
              fromSeqType(TypeTag(
                runtimeMirror(getClass.getClassLoader),
                new TypeCreator {
                  def apply[U <: Universe with Singleton](m: Mirror[U]) =
                    //m.staticClass("scala."+itemType.toString).selfType
                    itemType.asInstanceOf[U#Type]
                }
              ))
            }
          }
        }

        else if (tt.tpe <:< typeOf[Map[String, _]]) {
          tt.tpe match {
            case TypeRef(_, _, List(stringType, itemType)) => {
              fromMapType(TypeTag(
                runtimeMirror(getClass.getClassLoader),
                new TypeCreator {
                  def apply[U <: Universe with Singleton](m: Mirror[U]) =
                    //m.staticClass("scala."+itemType.toString).selfType
                    itemType.asInstanceOf[U#Type]
                }
              ))
            }
          }
        }

        else ??? // more complex types not handled yet
      }
    }

    avroType.asInstanceOf[AvroType[T]]
  }

  private def fromSeqType[A](itemType: TypeTag[A]) = new AvroArray()(itemType)

  private def fromMapType[A](itemType: TypeTag[A]) = new AvroMap()(itemType)

}