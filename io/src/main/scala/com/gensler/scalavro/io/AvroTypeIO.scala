package com.gensler.scalavro.io

import com.gensler.scalavro.error._
import com.gensler.scalavro.types.{AvroType, AvroPrimitiveType}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

trait AvroTypeIO[T] {

  /**
    * Returns the corresponding AvroType to this AvroTypeIO wrapper.
    */
  def avroType: AvroType[T]

  /**
    * Writes a serialized representation of the supplied object.  Throws an
    * AvroSerializationException if writing is unsuccessful. 
    */
  @throws[AvroSerializationException[_]]
  def write(obj: T, stream: OutputStream)

  /**
    * Attempts to create an object of type T by reading the required data from
    * the supplied stream.
    */
  @throws[AvroDeserializationException[_]]
  def read(stream: InputStream): Try[T]

  /**
    * Returns the JSON serialization of the supplied object.  Throws an
    * AvroSerializationException if writing is unsuccessful. 
    */
/*
  @throws[AvroSerializationException[_]]
  def writeAsJson(obj: T): JsValue
*/

  /**
    * Attempts to create an object of type T by reading the required data from
    * the supplied JSON source.
    */
/*
  @throws[AvroDeserializationException[_]]
  def readFromJson(json: String): Try[T]
*/

}

/**
  * Companion object for [[AvroTypeIO]]
  */
object AvroTypeIO {

  import scala.language.implicitConversions

  import com.gensler.scalavro.types.primitive._
  import com.gensler.scalavro.io.primitive._

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
    implicit def avroPrimitiveToIO[T](avroType: AvroPrimitiveType[T]): AvroTypeIO[T] =
      avroType match {
        case AvroBoolean => AvroBooleanIO
        case AvroBytes   => AvroBytesIO
        case AvroDouble  => AvroDoubleIO
        case AvroFloat   => AvroFloatIO
        case AvroInt     => AvroIntIO
        case AvroLong    => AvroLongIO
        case AvroNull    => AvroNullIO
        case AvroString  => AvroStringIO
      }

    // complex types
    implicit def arrayToIO[T](avroArray: AvroArray[T]): AvroTypeIO[Seq[T]]          = AvroArrayIO(avroArray)
    implicit def enumToIO[T](avroEnum: AvroEnum[T]): AvroTypeIO[T]                  = AvroEnumIO(avroEnum)
    implicit def fixedToIO[T](avroFixed: AvroFixed[T]): AvroTypeIO[T]               = AvroFixedIO(avroFixed)
    implicit def mapToIO[T](avroMap: AvroMap[T]): AvroTypeIO[Map[String, T]]        = AvroMapIO(avroMap)
    implicit def errorToIO[T](avroError: AvroError[T]): AvroTypeIO[T]               = AvroRecordIO(avroError)
    implicit def recordToIO[T](avroRecord: AvroRecord[T]): AvroTypeIO[T]            = AvroRecordIO(avroRecord)
    implicit def unionToIO[A, B](avroUnion: AvroUnion[A, B]): AvroTypeIO[Either[A, B]] = AvroUnionIO(avroUnion)
  }

}