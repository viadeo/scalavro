package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroFloat
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import spray.json._

object AvroFloatIO extends AvroFloatIO

trait AvroFloatIO extends AvroPrimitiveTypeIO[Float] {

  val avroType = AvroFloat

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: Float,
    encoder: BinaryEncoder): Unit = {

    encoder writeFloat value
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = decoder.readFloat

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: Float) = JsNumber(BigDecimal(value))

  def readJson(json: JsValue) = Try {
    json match {
      case JsNumber(bigDecimal) if bigDecimal.isValidFloat => bigDecimal.toFloat
      case _ => throw new AvroDeserializationException[Float]
    }
  }

}
