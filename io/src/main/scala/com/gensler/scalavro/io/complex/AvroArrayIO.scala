package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroArray
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroArrayIO[T](avroType: AvroArray[T]) extends AvroTypeIO[Seq[T]] {

  def write(obj: Seq[T], stream: OutputStream) = ???

  def read(stream: InputStream) = Try { ???.asInstanceOf[Seq[T]] }

}