package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import scala.util.{Try, Success}
import spray.json._
import java.io.{InputStream, OutputStream}

trait AvroNull extends AvroPrimitiveType[Unit] {
  val typeName = "null"
}

object AvroNull extends AvroNull