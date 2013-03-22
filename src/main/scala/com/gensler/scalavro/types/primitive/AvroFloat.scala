package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import scala.util.Try
import spray.json._
import java.io.{InputStream, OutputStream}

trait AvroFloat extends AvroPrimitiveType[Float] {

  val typeName = "float"

  def write(value: Float, stream: OutputStream) = {
    val bits: Int = java.lang.Float.floatToIntBits(value)
    stream write Array(
      bits.toByte,         // little endian
      (bits >> 8).toByte,
      (bits >> 16).toByte,
      (bits >> 24).toByte
    )
  }

  def writeAsJson(value: Float): JsValue = ???

  def read(stream: InputStream) = Try {
    ???.asInstanceOf[Float]
  }

  def readFromJson(json: JsValue) = Try { ???.asInstanceOf[Float] }

}

object AvroFloat extends AvroFloat