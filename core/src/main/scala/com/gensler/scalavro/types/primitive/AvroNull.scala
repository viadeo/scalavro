package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import scala.util.{Try, Success}
import spray.json._
import java.io.{InputStream, OutputStream}

trait AvroNull extends AvroPrimitiveType[Unit] {

  val typeName = "null"

  // null is written as zero bytes.
  def write(value: Unit, stream: OutputStream) {}

  def writeAsJson(value: Unit): JsValue = ???

  def read(stream: InputStream) = Success(())

  def readFromJson(json: JsValue) = Try { ???.asInstanceOf[Unit] }

}

object AvroNull extends AvroNull