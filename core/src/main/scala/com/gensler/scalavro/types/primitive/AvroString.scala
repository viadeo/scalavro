package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

trait AvroString extends AvroPrimitiveType[String] {
  val typeName = "string"
}

object AvroString extends AvroString