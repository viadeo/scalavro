package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroFixed
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericFixed

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroFixedIO[T](avroType: AvroFixed[T]) extends AvroTypeIO[T] {

  def asGeneric(obj: T): GenericFixed = ???

  def fromGeneric(obj: Any): T = obj match {
    case genericFixed: GenericFixed => {
      ???
    }
    case _ => throw new AvroDeserializationException()(avroType.tag)
  }

  def write(obj: T, stream: OutputStream) = ???

  def read(stream: InputStream) = Try { ???.asInstanceOf[T] }

}