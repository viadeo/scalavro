package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroJEnum
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}
import com.gensler.scalavro.util.ReflectionHelpers

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericEnumSymbol, GenericDatumWriter, GenericDatumReader}
import org.apache.avro.io.{EncoderFactory, DecoderFactory}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroJEnumIO[E](avroType: AvroJEnum[E]) extends AvroTypeIO[E] {

  protected lazy val avroSchema: Schema = (new Parser) parse avroType.selfContainedSchema().toString

  def asGeneric(obj: E): GenericEnumSymbol = new GenericData.EnumSymbol(avroSchema, obj.toString)

  def fromGeneric(obj: Any): E = obj match {
    case genericEnumSymbol: GenericEnumSymbol => avroType.symbolMap.get(genericEnumSymbol.toString).get
    case _ => throw new AvroDeserializationException[E]()(avroType.tag)
  }

  def write(obj: E, stream: OutputStream) = {
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

    val symbolName = avroType.symbols(com.gensler.scalavro.io.primitive.AvroIntIO.read(stream).get)
    avroType.symbolMap.get(symbolName).get

    // the following *should* work, but it appears to read too many bytes...
    // a bug in the reference implementation?

//  this fromGeneric datumReader.read(null, decoder)

  }

}