package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroShort
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ EncoderFactory, DecoderFactory }

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import java.io.{ InputStream, OutputStream }

object AvroShortIO extends AvroShortIO

trait AvroShortIO extends AvroTypeIO[Short] {

  def avroType = AvroShort

  protected[scalavro] def asGeneric[S <: Short: TypeTag](value: S): Short = value

  def write[S <: Short: TypeTag](value: S, stream: OutputStream) = {
    val encoder = EncoderFactory.get.directBinaryEncoder(stream, null)
    encoder writeInt value.toInt
  }

  def read(stream: InputStream) = Try {
    val decoder = DecoderFactory.get.directBinaryDecoder(stream, null)
    decoder.readInt.toShort
  }

}