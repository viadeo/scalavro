package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroFloat extends AvroType[Float] {

  val typeName = "float"

  def write(obj: Float): Array[Byte] = ???

  def read(bytes: Array[Byte]): Float = ???

}
