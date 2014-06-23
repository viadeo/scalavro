package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroJavaBoolean
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.util.Try

object AvroJavaBooleanIO extends AvroJavaBooleanIO

trait AvroJavaBooleanIO extends AvroNullablePrimitiveTypeIO[java.lang.Boolean] {

  val avroType = AvroJavaBoolean

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  /**
    * a boolean is written as a single byte whose value is either 0 (false) or
    * 1 (true).
    */
  protected[scalavro] def write(
    value: java.lang.Boolean,
    encoder: BinaryEncoder): Unit =
    if (value == null) {
      AvroLongIO.write(UNION_INDEX_NULL, encoder)
    }
    else {
      AvroLongIO.write(UNION_INDEX_VALUE, encoder)
      encoder writeBoolean value
    }

  protected[scalavro] def read(decoder: BinaryDecoder): java.lang.Boolean =
    AvroLongIO.read(decoder) match {
      case UNION_INDEX_NULL  => null
      case UNION_INDEX_VALUE => decoder.readBoolean
    }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: java.lang.Boolean) =
    if (value == null)
      JsNull
    else
      JsBoolean(value)

  def readJson(json: JsValue): Try[java.lang.Boolean] = Try {
    json match {
      case JsBoolean(value) => value
      case JsNull           => null
      case _                => throw new AvroDeserializationException[java.lang.Boolean]
    }
  }

}