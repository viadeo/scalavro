package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

trait AvroFloat extends AvroPrimitiveType[Float] {
  val typeName = "float"
}

object AvroFloat extends AvroFloat