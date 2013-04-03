package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

trait AvroBoolean extends AvroPrimitiveType[Boolean] {
  val typeName = "boolean"
}

object AvroBoolean extends AvroBoolean