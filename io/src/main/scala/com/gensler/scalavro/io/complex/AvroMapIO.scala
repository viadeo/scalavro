package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroMap
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroMapIO[T](avroType: AvroMap[T]) extends AvroTypeIO[Map[String, T]] {

  def write(obj: Map[String, T], stream: OutputStream) = ???

  def read(stream: InputStream) = Try { ???.asInstanceOf[Map[String, T]] }

}