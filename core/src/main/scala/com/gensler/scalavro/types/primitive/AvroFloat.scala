package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import com.gensler.scalavro.error.AvroDeserializationException
import scala.util.Try
import spray.json._
import java.io.{InputStream, OutputStream}

trait AvroFloat extends AvroPrimitiveType[Float] {
  val typeName = "float"
}

object AvroFloat extends AvroFloat