package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroByte
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import spray.json._

object AvroByteIO extends AvroByteIO

trait AvroByteIO extends AvroPrimitiveTypeIO[Byte] {

  val avroType = AvroByte

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: Byte,
    encoder: BinaryEncoder): Unit = {

    encoder writeInt value.toInt
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = decoder.readInt.toByte

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: Byte) = JsNumber(BigDecimal(value))

  def readJson(json: JsValue) = Try {
    json match {
      case JsNumber(bigDecimal) if bigDecimal.isValidByte => bigDecimal.toByte
      case _ => throw new AvroDeserializationException[Byte]
    }
  }

}