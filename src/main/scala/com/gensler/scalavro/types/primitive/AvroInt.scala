package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroInt extends AvroType[Int] {

  def write(obj: Int): Array[Byte] = ???

  def read(bytes: Array[Byte]): Int = ???

}
