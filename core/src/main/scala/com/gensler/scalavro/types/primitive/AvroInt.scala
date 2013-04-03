package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import com.gensler.scalavro.error.AvroDeserializationException
import scala.util.Try
import spray.json._
import java.io.{InputStream, OutputStream}

trait AvroInt extends AvroPrimitiveType[Int] {

  val typeName = "int"

  def write(value: Int, stream: OutputStream) = AvroLong.write(value, stream)

  def writeAsJson(value: Int): JsValue = ???

  def read(stream: InputStream) = Try {
    val long = AvroLong.read(stream).get
    if (long.isValidInt) long.toInt
    else throw new AvroDeserializationException[Int]
  }

  def readFromJson(json: JsValue) = Try { ???.asInstanceOf[Int] }

}

object AvroInt extends AvroInt