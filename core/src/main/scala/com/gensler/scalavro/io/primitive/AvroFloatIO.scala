package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroFloat
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ EncoderFactory, DecoderFactory, BinaryEncoder }

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import java.io.{ InputStream, OutputStream }

object AvroFloatIO extends AvroFloatIO

trait AvroFloatIO extends AvroTypeIO[Float] {

  def avroType = AvroFloat

  protected[scalavro] def asGeneric[F <: Float: TypeTag](value: F): Float = value

  def write[F <: Float: TypeTag](value: F, encoder: BinaryEncoder) = {
    encoder writeFloat value
    encoder.flush
  }

  def read(stream: InputStream) = Try {
    val decoder = DecoderFactory.get.directBinaryDecoder(stream, null)
    decoder.readFloat
  }
}
