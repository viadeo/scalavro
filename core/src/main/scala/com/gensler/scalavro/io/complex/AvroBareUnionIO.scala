package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive.AvroLongIO
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.AvroUnion
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import com.gensler.scalavro.util.ReflectionHelpers

import com.gensler.scalavro.util.Union
import com.gensler.scalavro.util.Union._

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

private[scalavro] case class AvroBareUnionIO[U <: Union.not[_]: TypeTag, T: TypeTag](
    avroType: AvroUnion[U, T]) extends AvroUnionIO[U, T] {

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write[X <: T: TypeTag](
    obj: X,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    ??? // Not Implemented!
  }

  def writeBare[X: prove[T]#containsType: TypeTag](
    obj: X,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean) = {

    val typeOfObj = ReflectionHelpers.classLoaderMirror.staticClass(obj.getClass.getName).toType
    val staticTypeOfObj = typeOf[X]
    avroType.memberAvroTypes.indexWhere { at => staticTypeOfObj <:< at.tag.tpe || typeOfObj <:< at.tag.tpe } match {
      case -1 => throw new AvroSerializationException(obj)
      case index: Int => {
        AvroLongIO.write(index.toLong, encoder)
        val memberType = avroType.memberAvroTypes(index).asInstanceOf[AvroType[T]]
        memberType.io.write(obj.asInstanceOf[T], encoder, references, false)
        encoder.flush
      }
    }
  }

  protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = {

    val index = AvroLongIO.read(decoder)
    val memberType = avroType.memberAvroTypes(index.toInt).asInstanceOf[AvroType[T]]
    memberType.io.read(decoder, references, false)
  }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writeJson[X <: T: TypeTag](obj: X) = {

    ??? // Not Implemented!
  }

  def readJson(json: JsValue) = Try {
    val memberInstance = json match {
      case JsNull => {
        resolveMemberTypeFromCompactSchema(AvroNull.compactSchema) match {
          case None           => throw new AvroDeserializationException[T]
          case Some(nullType) => Unit
        }
      }
      case JsObject(fields) if fields.size == 1 => {
        val (compactSchema, valueJson) = fields.head

        resolveMemberTypeFromCompactSchema(AvroNull.compactSchema) match {
          case None             => throw new AvroDeserializationException[T]
          case Some(memberType) => readJsonHelper(valueJson, memberType)
        }
      }
    }
    memberInstance.asInstanceOf[T]
  }

  protected[this] def resolveMemberTypeFromCompactSchema(schema: JsValue): Option[AvroType[_]] =
    avroType.memberAvroTypes.find { _.compactSchema == schema }

  protected[this] def readJsonHelper[A: TypeTag](json: JsValue, argType: AvroType[A]) =
    argType.io.readJson(json).get

}