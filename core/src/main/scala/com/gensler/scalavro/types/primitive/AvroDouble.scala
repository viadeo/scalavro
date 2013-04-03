package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

trait AvroDouble extends AvroPrimitiveType[Double] {
  val typeName = "double"
}

object AvroDouble extends AvroDouble