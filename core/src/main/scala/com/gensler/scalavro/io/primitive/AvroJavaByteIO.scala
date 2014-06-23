package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroJavaByte
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.util.Try

object AvroJavaByteIO extends AvroJavaByteIO

trait AvroJavaByteIO extends AvroNullablePrimitiveTypeIO[java.lang.Byte] {

  val avroType = AvroJavaByte

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: java.lang.Byte,
    encoder: BinaryEncoder): Unit =
    if (value == null) {
      AvroLongIO.write(UNION_INDEX_NULL, encoder)
    }
    else {
      AvroLongIO.write(UNION_INDEX_VALUE, encoder)
      encoder writeInt value.toInt
    }

  def read(decoder: BinaryDecoder): java.lang.Byte =
    AvroLongIO.read(decoder) match {
      case UNION_INDEX_NULL  => null
      case UNION_INDEX_VALUE => decoder.readInt.toByte
    }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: java.lang.Byte) =
    if (value == null)
      JsNull
    else
      JsNumber(BigDecimal(value.intValue))

  def readJson(json: JsValue): Try[java.lang.Byte] = Try {
    json match {
      case JsNumber(bigDecimal) if bigDecimal.isValidByte => bigDecimal.toByte
      case JsNull => null
      case _ => throw new AvroDeserializationException[java.lang.Byte]
    }
  }

}