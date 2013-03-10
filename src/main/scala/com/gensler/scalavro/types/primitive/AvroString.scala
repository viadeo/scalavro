package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType
import scala.util.Try

object AvroString extends AvroType[String] {

  val typeName = "string"

  def write(obj: String): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[String]
  }

}
