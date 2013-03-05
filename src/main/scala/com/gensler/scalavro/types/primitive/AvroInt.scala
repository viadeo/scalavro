package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroInt extends AvroType[Int] {

  val typeName = "int"

  def write(obj: Int): Array[Byte] = ???

  def read(bytes: Array[Byte]): Int = ???

}
