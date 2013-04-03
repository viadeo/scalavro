package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroInt
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

object AvroIntIO extends AvroIntIO

trait AvroIntIO extends AvroTypeIO[Int] {

  def avroType = AvroInt

  def write(value: Int, stream: OutputStream) = AvroLongIO.write(value, stream)

  def read(stream: InputStream) = Try {
    val long = AvroLongIO.read(stream).get
    if (long.isValidInt) long.toInt
    else throw new AvroDeserializationException[Int]
  }

}