package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroShort
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

object AvroShortIO extends AvroShortIO

trait AvroShortIO extends AvroTypeIO[Short] {

  val avroType = AvroShort

  def write[S <: Short: TypeTag](value: S, encoder: BinaryEncoder) = {
    encoder writeInt value.toInt
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = decoder.readInt.toShort

}