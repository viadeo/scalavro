package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroDouble
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

object AvroDoubleIO extends AvroDoubleIO

trait AvroDoubleIO extends AvroPrimitiveTypeIO[Double] {

  val avroType = AvroDouble

  protected[scalavro] def write(
    value: Double,
    encoder: BinaryEncoder): Unit = {

    encoder writeDouble value
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = decoder.readDouble

}