package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType

trait AvroBytes extends AvroPrimitiveType[Seq[Byte]] {
  val typeName = "bytes"
}

object AvroBytes extends AvroBytes