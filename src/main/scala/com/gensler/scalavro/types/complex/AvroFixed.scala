package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroNamedType

class AvroFixed[T] extends AvroNamedType[T] {

  def write(obj: T): Array[Byte] = ???

  def read(bytes: Array[Byte]): T = ???

}
