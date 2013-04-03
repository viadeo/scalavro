package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroFloat
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

object AvroFloatIO extends AvroFloatIO

trait AvroFloatIO extends AvroTypeIO[Float] {

  def avroType = AvroFloat

  def write(value: Float, stream: OutputStream) = {
    val bits: Int = java.lang.Float floatToIntBits value

    stream write Array(
      bits.toByte,        // little endian
      (bits >>> 8).toByte,
      (bits >>> 16).toByte,
      (bits >>> 24).toByte
    )
  }

  def read(stream: InputStream) = Try {
    val bytes = Array.ofDim[Byte](4)
    val bytesRead = stream read bytes
    if (bytesRead != 4) throw new AvroDeserializationException[Long]

    java.lang.Float.intBitsToFloat(
      (bytes(0)  & 0xFF).toInt        |
      ((bytes(1) & 0xFF).toInt << 8)  |
      ((bytes(2) & 0xFF).toInt << 16) |
      ((bytes(3) & 0xFF).toInt << 24)
    )
  }

}