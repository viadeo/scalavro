package com.gensler.scalavro.io

import com.gensler.scalavro.error._
import com.gensler.scalavro.types.{ AvroType, AvroPrimitiveType }
import com.gensler.scalavro.util.Logging

import org.apache.avro.io.{ EncoderFactory, BinaryEncoder }

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import java.io.{ InputStream, OutputStream }

abstract class AvroTypeIO[T: TypeTag] extends Logging {

  /**
    * Returns this AvroTypeIO instance.
    */
  def io: AvroTypeIO[T] = this

  /**
    * Returns the corresponding AvroType to this AvroTypeIO wrapper.
    */
  def avroType: AvroType[T]

  /**
    * Returns the `org.apache.avro.generic.GenericData` or primitive type
    * representation of the supplied object.
    */
  protected[scalavro] def asGeneric[G <: T: TypeTag](obj: G): Any

  /**
    * Writes a serialized representation of the supplied object according to
    * the Avro specification for binary encoding.  Throws an
    * AvroSerializationException if writing is unsuccessful.
    */
  @throws[AvroSerializationException[_]]
  final def write[G <: T: TypeTag](obj: G, stream: OutputStream): Unit = {
    val encoder = EncoderFactory.get.binaryEncoder(stream, null)
    write(obj, encoder)
    encoder.flush
  }

  /**
    * Writes a serialized representation of the supplied object according to
    * the Avro specification for binary encoding.  Throws an
    * AvroSerializationException if writing is unsuccessful.
    */
  @throws[AvroSerializationException[_]]
  def write[G <: T: TypeTag](obj: G, encoder: BinaryEncoder): Unit

  /**
    * Attempts to create an object of type T by reading the required data from
    * the supplied binary stream.
    */
  @throws[AvroDeserializationException[_]]
  def read(stream: InputStream): Try[T]

  /**
    * Writes a JSON serialization of the supplied object.  Throws an
    * AvroSerializationException if writing is unsuccessful.
    */
  /*
  @throws[AvroSerializationException[_]]
  def writeJson[G <: T : TypeTag](obj: G, stream: OutputStream)
*/

  /**
    * Attempts to create an object of type T by reading the required data from
    * the supplied JSON stream.
    */
  /*
  @throws[AvroDeserializationException[_]]
  def readJson(stream: InputStream): Try[T]
*/

}

/**
  * Companion object for [[AvroTypeIO]]
  */
object AvroTypeIO {

  import scala.language.implicitConversions

  import com.gensler.scalavro.types.primitive._
  import com.gensler.scalavro.io.primitive._
  import com.gensler.scalavro.util.Union
  import com.gensler.scalavro.util.FixedData

  import com.gensler.scalavro.types.complex._
  import com.gensler.scalavro.io.complex._

  /**
    * Contains implicit conversions from any AvroType to a corresponding
    * AvroTypeIO capable of reading and writing.
    *
    * Bring these members into scope to implicitly augment AvroType instances
    * with IO functionality as follows:
    *
    * {{{
    *   import com.gensler.scalavro.io.AvroTypeIO.Implicits._
    *
    *   // Given an avroType: AvroType[T] and an obj: T
    *   // it is possible to call write() directly:
    *
    *   avroType.write(obj, outputStream)
    * }}}
    */
  object Implicits {

    // primitive types
    implicit def avroTypeToIO[T](avroType: AvroPrimitiveType[T]): AvroTypeIO[T] =
      avroType match {
        case AvroBoolean => AvroBooleanIO
        case AvroBytes   => AvroBytesIO
        case AvroDouble  => AvroDoubleIO
        case AvroFloat   => AvroFloatIO
        case AvroByte    => AvroByteIO
        case AvroChar    => AvroCharIO
        case AvroShort   => AvroShortIO
        case AvroInt     => AvroIntIO
        case AvroLong    => AvroLongIO
        case AvroNull    => AvroNullIO
        case AvroString  => AvroStringIO
      }

    // complex types
    implicit def avroTypeToIO[T, S <: Seq[T]](array: AvroArray[T, S]): AvroArrayIO[T, S] = AvroArrayIO(array)
    implicit def avroTypeToIO[T, S <: Set[T]](set: AvroSet[T, S]): AvroSetIO[T, S] = AvroSetIO(set)
    implicit def avroTypeToIO[T <: Enumeration](enum: AvroEnum[T]): AvroEnumIO[T] = AvroEnumIO(enum)
    implicit def avroTypeToIO[T](enum: AvroJEnum[T]): AvroJEnumIO[T] = AvroJEnumIO(enum)
    implicit def avroTypeToIO[T <: FixedData](fixed: AvroFixed[T]): AvroFixedIO[T] = AvroFixedIO(fixed)(fixed.tag)
    implicit def avroTypeToIO[T, M <: Map[String, T]](map: AvroMap[T, M]): AvroMapIO[T, M] = AvroMapIO(map)
    implicit def avroTypeToIO[T](error: AvroError[T]): AvroRecordIO[T] = AvroRecordIO(error)
    implicit def avroTypeToIO[T](record: AvroRecord[T]): AvroRecordIO[T] = AvroRecordIO(record)
    implicit def avroTypeToIO[U <: Union.not[_], T](union: AvroUnion[U, T]): AvroUnionIO[U, T] = AvroUnionIO(union)(union.union.underlyingTag, union.tag)

    implicit def avroTypeToIO[T: TypeTag](at: AvroType[T]): AvroTypeIO[T] = {
      at match {
        case t: AvroPrimitiveType[_] => avroTypeToIO(t)
        case t: AvroArray[_, _]      => avroTypeToIO(t)
        case t: AvroSet[_, _]        => avroTypeToIO(t)
        case t: AvroEnum[_]          => avroTypeToIO(t)
        case t: AvroJEnum[_]         => avroTypeToIO(t)
        case t: AvroFixed[_]         => avroTypeToIO(t)
        case t: AvroMap[_, _]        => avroTypeToIO(t)
        case t: AvroError[_]         => avroTypeToIO(t)
        case t: AvroRecord[_]        => avroTypeToIO(t)
        case t: AvroUnion[_, _]      => avroTypeToIO(t)
      }
    }.asInstanceOf[AvroTypeIO[T]]

  }

}