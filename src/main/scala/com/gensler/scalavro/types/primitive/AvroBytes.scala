package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import com.gensler.scalavro.error._
import scala.util.Try
import spray.json._
import java.io.{InputStream, OutputStream}

trait AvroBytes extends AvroPrimitiveType[Seq[Byte]] {

  val typeName = "bytes"

  def write(bytes: Seq[Byte], stream: OutputStream) = {
    AvroLong.write(bytes.length, stream)
    stream.write(bytes.toArray)
  }

  def writeAsJson(bytes: Seq[Byte]): JsValue = ???

  def read(stream: InputStream) = Try {
    val length = AvroLong.read(stream).get
    val buffer = Array.ofDim[Byte](length.toInt)
    val bytesRead = stream read buffer
    if (bytesRead != length) throw new AvroDeserializationException[Seq[Byte]]
    buffer.toSeq
  }

  def readFromJson(json: JsValue) = Try { ???.asInstanceOf[Seq[Byte]] }

}

object AvroBytes extends AvroBytes