package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroLong
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

object AvroLongIO extends AvroLongIO

trait AvroLongIO extends AvroPrimitiveTypeIO[Long] {

  val avroType = AvroLong

  protected[scalavro] def write(
    value: Long,
    encoder: BinaryEncoder): Unit = {

    encoder writeLong value
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = decoder.readLong

}