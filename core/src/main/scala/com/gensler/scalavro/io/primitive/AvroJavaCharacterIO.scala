package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroJavaCharacter
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.util.Try

object AvroJavaCharacterIO extends AvroJavaCharacterIO

trait AvroJavaCharacterIO extends AvroNullablePrimitiveTypeIO[java.lang.Character] {

  val avroType = AvroJavaCharacter

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: java.lang.Character,
    encoder: BinaryEncoder): Unit =
    if (value == null) {
      AvroLongIO.write(UNION_INDEX_NULL, encoder)
    }
    else {
      AvroLongIO.write(UNION_INDEX_VALUE, encoder)
      encoder writeInt value.toChar
    }

  def read(decoder: BinaryDecoder): java.lang.Character =
    AvroLongIO.read(decoder) match {
      case UNION_INDEX_NULL  => null
      case UNION_INDEX_VALUE => decoder.readInt.toChar
    }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: java.lang.Character) =
    if (value == null)
      JsNull
    else
      JsString(value.toString)

  def readJson(json: JsValue): Try[java.lang.Character] = Try {
    json match {
      case JsString(value) if value.length == 1 => value.head
      case JsNull                               => null
      case _                                    => throw new AvroDeserializationException[java.lang.Character]
    }
  }

}