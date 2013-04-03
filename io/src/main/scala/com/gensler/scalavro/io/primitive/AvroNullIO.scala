package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

object AvroNullIO extends AvroNullIO

trait AvroNullIO extends AvroTypeIO[Unit] {

  def avroType = AvroNull

  // null is written as zero bytes.
  def write(value: Unit, stream: OutputStream) {}

  def read(stream: InputStream) = Success(())

}