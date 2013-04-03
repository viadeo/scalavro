package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

trait AvroInt extends AvroPrimitiveType[Int] {
  val typeName = "int"
}

object AvroInt extends AvroInt