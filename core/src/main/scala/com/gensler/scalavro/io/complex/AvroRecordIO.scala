package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.AvroPrimitiveType
import com.gensler.scalavro.types.complex.AvroRecord
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }
import com.gensler.scalavro.util.ReflectionHelpers

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{ GenericRecord, GenericData, GenericDatumWriter }
import org.apache.avro.io.EncoderFactory

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.{ TypeTag, typeTag }

import java.io.{ InputStream, OutputStream }

case class AvroRecordIO[T](avroType: AvroRecord[T]) extends AvroTypeIO[T]()(avroType.tag) {

  implicit val tt: TypeTag[T] = avroType.tag

  import ReflectionHelpers.{ ProductElementExtractor, CaseClassFactory }

  protected[this] lazy val extractors: Map[String, ProductElementExtractor[T, _]] = {
    avroType.fields.map { field => field.name -> extractorFor(field) }.toMap
  }

  private def extractorFor[F](field: AvroRecord.Field[F]): ProductElementExtractor[T, F] = {
    implicit val ft: TypeTag[F] = field.fieldType.tag
    new ProductElementExtractor[T, F](field.name)
  }

  protected[this] lazy val factory = new CaseClassFactory[T]

  protected[this] lazy val fieldReaders: Seq[AvroTypeIO[_]] = avroType.fields.map { _.fieldType.io }

  /**
    * Writes a binary representation of the supplied object to the supplied
    * stream.
    */
  def write[R <: T: TypeTag](obj: R, encoder: BinaryEncoder) {
    for (field <- avroType.fields) {
      try {
        val value = extractors(field.name).extractFrom(obj).asInstanceOf[Any]
        val fieldTag = field.fieldType.tag.asInstanceOf[TypeTag[Any]]
        field.fieldType.io.asInstanceOf[AvroTypeIO[Any]].write(value, encoder)(fieldTag)
      }
      catch {
        case cause: Throwable => throw new AvroSerializationException(
          obj,
          cause,
          "Could not extract a value for field [%s]" format field.name
        )
      }
    }
  }

  /**
    * Reads a binary representation of the underlying Scala type from the
    * supplied stream.
    */
  def read(decoder: BinaryDecoder) = {
    val args = new scala.collection.mutable.ArrayBuffer[Any](initialSize = avroType.fields.size)
    for (reader <- fieldReaders) args += reader read decoder
    factory buildWith args
  }

}