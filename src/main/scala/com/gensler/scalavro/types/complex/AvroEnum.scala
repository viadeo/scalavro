package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroNamedType

class AvroEnum[T] extends AvroNamedType[T] {

  val typeName = "enum"

  def write(obj: T): Seq[Byte] = ???

  def read(bytes: Seq[Byte]): T = ???

}

