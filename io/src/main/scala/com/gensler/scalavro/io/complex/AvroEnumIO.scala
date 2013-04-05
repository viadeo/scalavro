package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroEnum
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericEnumSymbol

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroEnumIO[T](avroType: AvroEnum[T]) extends AvroTypeIO[T] {

  def asGeneric(obj: T): GenericEnumSymbol = ???

  def fromGeneric(obj: Any): T = ???

  def write(obj: T, stream: OutputStream) = ???

  def read(stream: InputStream) = Try { ???.asInstanceOf[T] }

}