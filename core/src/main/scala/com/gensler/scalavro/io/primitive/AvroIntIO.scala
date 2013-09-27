package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroInt
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

object AvroIntIO extends AvroIntIO

trait AvroIntIO extends AvroTypeIO[Int] {

  val avroType = AvroInt

  protected[scalavro] def write[I <: Int: TypeTag](
    value: I,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    encoder writeInt value
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = decoder.readInt

}