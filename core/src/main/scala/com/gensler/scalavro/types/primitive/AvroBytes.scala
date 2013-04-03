package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import com.gensler.scalavro.error._
import scala.util.Try
import spray.json._
import java.io.{InputStream, OutputStream}

trait AvroBytes extends AvroPrimitiveType[Seq[Byte]] {
  val typeName = "bytes"
}

object AvroBytes extends AvroBytes