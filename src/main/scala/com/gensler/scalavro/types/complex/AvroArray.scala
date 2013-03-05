package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroType

class AvroArray[T] extends AvroType[Array[T]] {

  val typeName = "array"

  def write(obj: Array[T]): Array[Byte] = ???

  def read(bytes: Array[Byte]): Array[T] = ???

}
