package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroChar
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

object AvroCharIO extends AvroCharIO

trait AvroCharIO extends AvroPrimitiveTypeIO[Char] {

  val avroType = AvroChar

  protected[scalavro] def write(
    value: Char,
    encoder: BinaryEncoder): Unit = {

    encoder writeInt value.toChar
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = decoder.readInt.toChar

}