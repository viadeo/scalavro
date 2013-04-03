package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroBoolean
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

object AvroBooleanIO extends AvroBooleanIO

trait AvroBooleanIO extends AvroTypeIO[Boolean] {

  def avroType = AvroBoolean

  final val trueByte = 1.toByte
  final val falseByte = 0.toByte

  /**
    * a boolean is written as a single byte whose value is either 0 (false) or
    * 1 (true).
    */
  def write(value: Boolean, stream: OutputStream) =
    stream.write { if (value) trueByte else falseByte }

  def read(stream: InputStream) = Try { stream.read match {
    case `trueByte` =>  true
    case `falseByte` => false
    case _ => throw new AvroDeserializationException[Boolean]
  }}

}