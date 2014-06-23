package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroJavaShort
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.util.Try

object AvroJavaShortIO extends AvroJavaShortIO

trait AvroJavaShortIO extends AvroNullablePrimitiveTypeIO[java.lang.Short] {

  val avroType = AvroJavaShort

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: java.lang.Short,
    encoder: BinaryEncoder): Unit =
    if (value == null) {
      AvroLongIO.write(UNION_INDEX_NULL, encoder)
    }
    else {
      AvroLongIO.write(UNION_INDEX_VALUE, encoder)
      encoder writeInt value.toInt
    }

  def read(decoder: BinaryDecoder): java.lang.Short =
    AvroLongIO.read(decoder) match {
      case UNION_INDEX_NULL  => null
      case UNION_INDEX_VALUE => decoder.readInt.toShort
    }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: java.lang.Short) =
    if (value == null)
      JsNull
    else
      JsNumber(BigDecimal(value.intValue))

  def readJson(json: JsValue): Try[java.lang.Short] = Try {
    json match {
      case JsNumber(bigDecimal) if bigDecimal.isValidShort => bigDecimal.toShort
      case JsNull => null
      case _ => throw new AvroDeserializationException[java.lang.Short]
    }
  }

}