package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import com.gensler.scalavro.error.AvroDeserializationException
import scala.util.Try
import spray.json._
import java.io.{InputStream, OutputStream}

trait AvroDouble extends AvroPrimitiveType[Double] {
  val typeName = "double"
}

object AvroDouble extends AvroDouble