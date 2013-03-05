package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroBytes extends AvroType[Seq[Byte]] {

  val typeName = "bytes"

  def write(obj: Seq[Byte]): Seq[Byte] = ???

  def read(bytes: Seq[Byte]): Seq[Byte] = ???

}
