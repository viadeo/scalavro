package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroString extends AvroType[String] {

  def write(obj: String): Array[Byte] = ???

  def read(bytes: Array[Byte]): String = ???

}
