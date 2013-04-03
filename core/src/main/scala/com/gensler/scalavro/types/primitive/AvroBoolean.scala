package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import com.gensler.scalavro.error._
import scala.util.Try
import spray.json._
import java.io.{InputStream, OutputStream}

trait AvroBoolean extends AvroPrimitiveType[Boolean] with DefaultJsonProtocol {

  val typeName = "boolean"

  final val trueByte = 1.toByte
  final val falseByte = 0.toByte

  /**
    * a boolean is written as a single byte whose value is either 0 (false) or
    * 1 (true).
    */
  def write(value: Boolean, stream: OutputStream) =
    stream.write { if (value) trueByte else falseByte }

  def writeAsJson(value: Boolean): JsValue = value.toJson

  def read(stream: InputStream) = Try { stream.read match {
    case `trueByte` =>  true
    case `falseByte` => false
    case _ => throw new AvroDeserializationException[Boolean]
  }}

  def readFromJson(json: JsValue) = Try { json match {
    case JsBoolean(value) => value
    case _ => throw new AvroSerializationException
  }}

}

object AvroBoolean extends AvroBoolean