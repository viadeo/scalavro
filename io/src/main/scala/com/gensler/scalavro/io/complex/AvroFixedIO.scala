package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroFixed
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericFixed

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroFixedIO(avroType: AvroFixed) extends AvroTypeIO[Seq[Byte]] {

  protected lazy val avroSchema: Schema = (new Parser) parse avroType.selfContainedSchema().toString

  def asGeneric(obj: Seq[Byte]): GenericFixed = obj match {
    case bytes: Seq[_] => {
      new GenericData.Fixed(avroSchema, bytes.asInstanceOf[Seq[Byte]].toArray)
    }
    case _ => throw new AvroSerializationException(obj)(avroType.tag)
  }

  def fromGeneric(obj: Any): Seq[Byte] = obj match {
    case genericFixed: GenericFixed => {
      if (genericFixed.bytes.length == avroType.size) genericFixed.bytes.toSeq
      else throw new AvroDeserializationException()(avroType.tag)
    }
    case _ => throw new AvroDeserializationException()(avroType.tag)
  }

  def write(obj: Seq[Byte], stream: OutputStream) = ???

  def read(stream: InputStream) = Try { ???.asInstanceOf[Seq[Byte]] }

}