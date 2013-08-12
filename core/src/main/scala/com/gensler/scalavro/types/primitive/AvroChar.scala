package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

trait AvroChar extends AvroPrimitiveType[Char] {
  val typeName = "int"
}

object AvroChar extends AvroChar