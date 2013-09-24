package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroBoolean
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import java.io.{ InputStream, OutputStream }

object AvroBooleanIO extends AvroBooleanIO

trait AvroBooleanIO extends AvroTypeIO[Boolean] {

  val avroType = AvroBoolean

  /**
    * a boolean is written as a single byte whose value is either 0 (false) or
    * 1 (true).
    */
  def write[B <: Boolean: TypeTag](value: B, encoder: BinaryEncoder) = {
    encoder writeBoolean value
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = decoder.readBoolean

}