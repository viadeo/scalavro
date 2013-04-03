package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroBytes
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

object AvroBytesIO extends AvroBytesIO

trait AvroBytesIO extends AvroTypeIO[Seq[Byte]] {

  def avroType = AvroBytes

  def write(bytes: Seq[Byte], stream: OutputStream) = {
    AvroLongIO.write(bytes.length, stream)
    stream.write(bytes.toArray)
  }

  def read(stream: InputStream) = Try {
    val length = AvroLongIO.read(stream).get
    val buffer = Array.ofDim[Byte](length.toInt)
    val bytesRead = stream read buffer
    if (bytesRead != length) throw new AvroDeserializationException[Seq[Byte]]
    buffer.toSeq
  }

}