package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroBoolean

import scala.collection.mutable
import scala.reflect.runtime.universe.TypeTag

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

object AvroBooleanIO extends AvroBooleanIO

trait AvroBooleanIO extends AvroPrimitiveTypeIO[Boolean] {

  val avroType = AvroBoolean

  /**
    * a boolean is written as a single byte whose value is either 0 (false) or
    * 1 (true).
    */
  protected[scalavro] def write(
    value: Boolean,
    encoder: BinaryEncoder): Unit = {

    encoder writeBoolean value
    encoder.flush
  }

  protected[scalavro] def read(decoder: BinaryDecoder) = decoder.readBoolean

}