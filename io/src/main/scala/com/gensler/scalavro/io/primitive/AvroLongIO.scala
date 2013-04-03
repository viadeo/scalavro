package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroLong
import com.gensler.scalavro.util.Varint
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream, DataInputStream, DataOutputStream}

object AvroLongIO extends AvroLongIO

trait AvroLongIO extends AvroTypeIO[Long] {

  def avroType = AvroLong

  def write(value: Long, stream: OutputStream) =
    Varint.writeSignedVarLong(value, new DataOutputStream(stream))

  def read(stream: InputStream) = Try {
    Varint.readSignedVarLong(new DataInputStream(stream))
  }

}