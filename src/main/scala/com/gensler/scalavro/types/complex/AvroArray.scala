package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroType

class AvroArray[T] extends AvroType[Seq[T]] {

  val typeName = "array"

  def write(obj: Seq[T]): Seq[Byte] = ???

  def read(bytes: Seq[Byte]): Seq[T] = ???

}
