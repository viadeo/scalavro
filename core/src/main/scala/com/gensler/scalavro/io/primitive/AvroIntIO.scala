package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroInt
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import spray.json._

object AvroIntIO extends AvroIntIO

trait AvroIntIO extends AvroPrimitiveTypeIO[Int] {

  val avroType = AvroInt

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: Int,
    encoder: BinaryEncoder): Unit = {

    encoder writeInt value
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = decoder.readInt

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: Int) = JsNumber(BigDecimal(value))

  def readJson(json: JsValue) = Try {
    json match {
      case JsNumber(bigDecimal) if bigDecimal.isValidInt => bigDecimal.toInt
      case _ => throw new AvroDeserializationException[Int]
    }
  }

}