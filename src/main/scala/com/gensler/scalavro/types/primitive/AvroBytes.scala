package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroBytes extends AvroType[Seq[Byte]] {

  def write(obj: Seq[Byte]): Array[Byte] = ???

  def read(bytes: Array[Byte]): Seq[Byte] = ???

}
