package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import scala.util.Try
import spray.json._
import java.io.{InputStream, OutputStream}

trait AvroBoolean extends AvroPrimitiveType[Boolean] with DefaultJsonProtocol {
  val typeName = "boolean"
}

object AvroBoolean extends AvroBoolean