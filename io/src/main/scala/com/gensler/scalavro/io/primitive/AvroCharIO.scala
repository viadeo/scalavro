package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroChar
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ EncoderFactory, DecoderFactory }

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import java.io.{ InputStream, OutputStream }

object AvroCharIO extends AvroCharIO

trait AvroCharIO extends AvroTypeIO[Char] {

  def avroType = AvroChar

  protected[scalavro] def asGeneric[C <: Char: TypeTag](value: C): Char = value

  def write[C <: Char: TypeTag](value: C, stream: OutputStream) = {
    val encoder = EncoderFactory.get.directBinaryEncoder(stream, null)
    encoder writeInt value.toChar
  }

  def read(stream: InputStream) = Try {
    val decoder = DecoderFactory.get.directBinaryDecoder(stream, null)
    decoder.readInt.toChar
  }

}