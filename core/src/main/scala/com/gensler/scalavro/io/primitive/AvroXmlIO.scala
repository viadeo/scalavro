package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroXml
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.generic.GenericData
import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.util.Try
import scala.xml.{ XML, Node }

object AvroXmlIO extends AvroXmlIO

trait AvroXmlIO extends AvroNullablePrimitiveTypeIO[Node] {

  val avroType = AvroXml

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: Node,
    encoder: BinaryEncoder): Unit = AvroStringIO.write(value.toString, encoder)

  def read(decoder: BinaryDecoder) =
    AvroStringIO.read(decoder) match {
      case xml: String => XML.loadString(xml)
      case null        => null
    }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: Node) =
    if (value == null)
      JsNull
    else
      JsString(value.toString)

  def readJson(json: JsValue) = Try {
    json match {
      case JsString(value) => XML loadString value
      case JsNull          => null
      case _               => throw new AvroDeserializationException[Node]
    }
  }

}
