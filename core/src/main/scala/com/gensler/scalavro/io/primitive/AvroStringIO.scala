package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroString
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.generic.GenericData
import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

object AvroStringIO extends AvroStringIO

trait AvroStringIO extends AvroPrimitiveTypeIO[String] {

  val avroType = AvroString

  protected[scalavro] def write(
    value: String,
    encoder: BinaryEncoder): Unit = {

    encoder writeString value
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = decoder.readString

}
