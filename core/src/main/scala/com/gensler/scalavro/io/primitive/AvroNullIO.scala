package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

object AvroNullIO extends AvroNullIO

trait AvroNullIO extends AvroPrimitiveTypeIO[Unit] {

  val avroType = AvroNull

  // null is written as zero bytes.

  protected[scalavro] def write(
    value: Unit,
    encoder: BinaryEncoder): Unit = {}

  def read(decoder: BinaryDecoder) = ()

}