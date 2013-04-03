package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroUnion
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroUnionIO[A, B](avroType: AvroUnion[A, B]) extends AvroTypeIO[Either[A, B]] {

  def write(obj: Either[A, B], stream: OutputStream) = obj match {
    case Left(a)  => ???
    case Right(b) => ???
  }

  def read(stream: InputStream) = Try { ???.asInstanceOf[Either[A, B]] }

}