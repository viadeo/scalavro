package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroBytes
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import java.nio.ByteBuffer

object AvroBytesIO extends AvroBytesIO

trait AvroBytesIO extends AvroTypeIO[Seq[Byte]] {

  val avroType = AvroBytes

  protected[scalavro] def write[B <: Seq[Byte]: TypeTag](
    bytes: B,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    encoder writeBytes bytes.toArray
    encoder.flush
  }

  def read(decoder: BinaryDecoder) = {
    val numBytes = decoder.readLong
    val buffer = Array.ofDim[Byte](numBytes.toInt)
    decoder.readFixed(buffer)
    buffer.toIndexedSeq
  }

}