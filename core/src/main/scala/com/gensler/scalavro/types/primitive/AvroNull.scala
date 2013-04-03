package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

trait AvroNull extends AvroPrimitiveType[Unit] {
  val typeName = "null"
}

object AvroNull extends AvroNull