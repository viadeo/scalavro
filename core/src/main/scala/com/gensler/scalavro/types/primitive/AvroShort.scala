package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

trait AvroShort extends AvroPrimitiveType[Short] {
  val typeName = "int"
}

object AvroShort extends AvroShort