package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import com.gensler.scalavro.error.AvroDeserializationException
import scala.util.Try
import spray.json._
import java.io.{InputStream, OutputStream}

trait AvroInt extends AvroPrimitiveType[Int] {
  val typeName = "int"
}

object AvroInt extends AvroInt