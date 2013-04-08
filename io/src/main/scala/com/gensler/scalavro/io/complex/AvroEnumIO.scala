package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroEnum
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}
import com.gensler.scalavro.util.ReflectionHelpers

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericEnumSymbol, GenericDatumWriter, GenericDatumReader}
import org.apache.avro.io.{EncoderFactory, DecoderFactory}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroEnumIO[E <: Enumeration](avroType: AvroEnum[E]) extends AvroTypeIO[E#Value] {

  // AvroEnum exposes two TypeTags:
  //   `tag` is the TypeTag for the enum values
  //   `enumTag` is the TypeTag of the enum itself

  protected lazy val avroSchema: Schema = (new Parser) parse avroType.selfContainedSchema().toString

  val moduleMirror = ReflectionHelpers.classLoaderMirror.reflectModule {
    avroType.enumTag.tpe.typeSymbol.asClass.module.asModule
  }

  val enumeration = moduleMirror.instance.asInstanceOf[E]

  def asGeneric(obj: E#Value): GenericEnumSymbol = obj match {
    case value: E#Value => new GenericData.EnumSymbol(avroSchema, value.toString)
    case _ => throw new AvroSerializationException(obj)(avroType.tag)
  }

  def fromGeneric(obj: Any): E#Value = obj match {
    case genericEnumSymbol: GenericEnumSymbol => enumeration withName genericEnumSymbol.toString
    case _ => throw new AvroDeserializationException[E#Value]()(avroType.tag)
  }

  def write(obj: E#Value, stream: OutputStream) = {
    try {
      val datumWriter = new GenericDatumWriter[GenericEnumSymbol](avroSchema)
      val encoder = EncoderFactory.get.binaryEncoder(stream, null)
      datumWriter.write(asGeneric(obj), encoder)
      encoder.flush
    }
    catch { case cause: Throwable => 
      throw new AvroSerializationException(obj, cause)(avroType.tag)
    }
  }

  def read(stream: InputStream) = Try {
    val datumReader = new GenericDatumReader[GenericEnumSymbol](avroSchema)
    val decoder = DecoderFactory.get.binaryDecoder(stream, null)

    enumeration(com.gensler.scalavro.io.primitive.AvroIntIO.read(stream).get)

    // the following *should* work, but it appears to read too many bytes...
    // a bug in the reference implementation?

//  this fromGeneric datumReader.read(null, decoder)

  }

}