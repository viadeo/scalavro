package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

trait AvroByte extends AvroPrimitiveType[Byte] {
  val typeName = "int"
}

object AvroByte extends AvroByte