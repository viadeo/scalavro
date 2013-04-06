package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroArray
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericArray, GenericDatumWriter, GenericDatumReader}
import org.apache.avro.io.{EncoderFactory, DecoderFactory}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroArrayIO[T](avroType: AvroArray[T]) extends AvroTypeIO[Seq[T]] {

  implicit def itemTypeTag = avroType.itemType.tag

  lazy val avroSchema: Schema = (new Parser) parse avroType.selfContainedSchema().toString

  val itemIO = AvroTypeIO.Implicits.avroTypeToIO(avroType.itemType)

  def asGeneric(items: Seq[T]): GenericArray[Any] = {
    import scala.collection.JavaConversions.seqAsJavaList
    val genericArray = new GenericData.Array[Any](items.size, avroSchema)
    genericArray addAll seqAsJavaList(items map { itemIO.asGeneric })
    genericArray
  }

  def fromGeneric(obj: Any): Seq[T] = {
    import scala.collection.JavaConversions.asScalaBuffer
    obj match {
      case genericArray: GenericArray[_] => {
        val genericSeq = asScalaBuffer(genericArray)
        genericSeq.map { itemIO.fromGeneric }
      }
      case _ => throw new AvroDeserializationException()(avroType.tag)
    }
  }

  def write(obj: Seq[T], stream: OutputStream) = {
    try {
      val encoder = EncoderFactory.get.binaryEncoder(stream, null)
      val datumWriter = new GenericDatumWriter[GenericArray[_]](avroSchema)
      datumWriter.write(asGeneric(obj), encoder)
      encoder.flush
    }
    catch { case cause: Throwable => 
      throw new AvroSerializationException(obj, cause)(avroType.tag)
    }
  }

  def read(stream: InputStream) = Try {
    val datumReader = new GenericDatumReader[GenericArray[_]](avroSchema)
    val decoder = DecoderFactory.get.binaryDecoder(stream, null)
    this fromGeneric datumReader.read(null, decoder)
  }

}