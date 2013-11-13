package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroShort
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import spray.json._

object AvroShortIO extends AvroShortIO

trait AvroShortIO extends AvroPrimitiveTypeIO[Short] {

  val avroType = AvroShort

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: Short,
    encoder: BinaryEncoder): Unit = {

    encoder writeInt value.toInt
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = decoder.readInt.toShort

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: Short) = JsNumber(BigDecimal(value))

  def readJson(json: JsValue) = Try {
    json match {
      case JsNumber(bigDecimal) if bigDecimal.isValidShort => bigDecimal.toShort
      case _ => throw new AvroDeserializationException[Short]
    }
  }

}