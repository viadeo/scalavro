package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroJavaDouble
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.util.Try

object AvroJavaDoubleIO extends AvroJavaDoubleIO

trait AvroJavaDoubleIO extends AvroNullablePrimitiveTypeIO[java.lang.Double] {

  val avroType = AvroJavaDouble

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: java.lang.Double,
    encoder: BinaryEncoder): Unit =
    if (value == null) {
      AvroLongIO.write(UNION_INDEX_NULL, encoder)
    }
    else {
      AvroLongIO.write(UNION_INDEX_VALUE, encoder)
      encoder writeDouble value
    }

  def read(decoder: BinaryDecoder): java.lang.Double =
    AvroLongIO.read(decoder) match {
      case UNION_INDEX_NULL  => null
      case UNION_INDEX_VALUE => decoder.readDouble
    }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: java.lang.Double) =
    if (value == null)
      JsNull
    else
      JsNumber(BigDecimal(value))

  def readJson(json: JsValue): Try[java.lang.Double] = Try {
    json match {
      case JsNumber(bigDecimal) => bigDecimal.toDouble
      case JsNull               => null
      case _                    => throw new AvroDeserializationException[java.lang.Double]
    }
  }

}