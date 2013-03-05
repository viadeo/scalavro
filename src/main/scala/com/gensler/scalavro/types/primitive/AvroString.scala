package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroString extends AvroType[String] {

  val typeName = "string"

  def write(obj: String): Seq[Byte] = ???

  def read(bytes: Seq[Byte]): String = ???

}
