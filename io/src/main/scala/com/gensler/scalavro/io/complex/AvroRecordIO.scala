package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroRecord
import com.gensler.scalavro.util.ReflectionHelpers
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericRecord, GenericData, GenericDatumWriter, GenericDatumReader}
import org.apache.avro.io.{EncoderFactory, DecoderFactory}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroRecordIO[T](avroType: AvroRecord[T]) extends AvroTypeIO[T] {

  // result of Apache implementation's Schema.Parser.parse
  protected lazy val avroSchema: Schema = (new Parser) parse avroType.schema.toString

  /**
    * Returns the Apache implementation's GenericRecord representation of this
    * AvroRecord.
    */
  protected[scalavro] def asGenericRecord(obj: T): GenericRecord = {
    val record = new GenericData.Record(avroSchema)

    avroType.fields.foreach { field =>
      ReflectionHelpers.productElement(obj, field.name)(avroType.tag, field.fieldType.tag) map { value =>
        record.put(field.name, value) // primitives only for now...
      }
    }

    record
  }

  /**
    * Writes a binary representation of the supplied object to the supplied
    * stream.
    */
  def write(obj: T, stream: OutputStream) {
    try {
      val encoder = EncoderFactory.get.binaryEncoder(stream, null)
      val datumWriter = new GenericDatumWriter[GenericRecord](avroSchema)
      datumWriter.write(asGenericRecord(obj), encoder)
      encoder.flush
    }
    catch { case t: Throwable => throw new AvroSerializationException(obj)(avroType.tag) }
  }

  /**
    * Reads a binary representation of the underlying Scala type from the
    * supplied stream.
    */
  def read(stream: InputStream) = Try {
    val datumReader = new GenericDatumReader[GenericRecord](avroSchema)
    val decoder = DecoderFactory.get.binaryDecoder(stream, null)
    val record = datumReader.read(null.asInstanceOf[GenericRecord], decoder)

    val args = avroType.fields map { field => record.get(field.name) match {
      case utf8: org.apache.avro.util.Utf8 => utf8.toString
      case other: Any => other
    }}

    ReflectionHelpers.instantiateCaseClassWith(args)(avroType.tag).get
  }

}