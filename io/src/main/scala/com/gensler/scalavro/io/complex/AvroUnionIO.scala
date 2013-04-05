package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroUnion
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}
import com.gensler.scalavro.io.AvroTypeIO.Implicits._

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroUnionIO[A, B](avroType: AvroUnion[A, B]) extends AvroTypeIO[Either[A, B]] {

  implicit def aTypeTag = avroType.leftType.tag
  implicit def bTypeTag = avroType.rightType.tag

  def asGeneric(obj: Either[A, B]) = {
    obj match {
      case Left(a)  => avroType.leftType.asGeneric(a)
      case Right(b) => avroType.rightType.asGeneric(b)
    }
  }

  def fromGeneric(obj: Any): Either[A, B] = {
    val genericA = Try { Left(avroType.leftType fromGeneric obj) }.toOption
    genericA getOrElse Right(avroType.rightType fromGeneric obj)
  }

  def write(obj: Either[A, B], stream: OutputStream) = obj match {
    case Left(a)  => ???
    case Right(b) => ???
  }

  def read(stream: InputStream) = Try { ???.asInstanceOf[Either[A, B]] }

}