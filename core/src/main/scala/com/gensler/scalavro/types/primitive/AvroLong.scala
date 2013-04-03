package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import com.gensler.scalavro.util.Varint
import scala.util.Try
import spray.json._
import java.io.{ InputStream, OutputStream, DataInputStream, DataOutputStream }

trait AvroLong extends AvroPrimitiveType[Long] {

  val typeName = "long"

  def write(value: Long, stream: OutputStream) =
    Varint.writeSignedVarLong(value, new DataOutputStream(stream))

  def writeAsJson(value: Long): JsValue = ???

  def read(stream: InputStream) = Try {
    Varint.readSignedVarLong(new DataInputStream(stream))
  }

  def readFromJson(json: JsValue) = Try { ???.asInstanceOf[Long] }
}

object AvroLong extends AvroLong