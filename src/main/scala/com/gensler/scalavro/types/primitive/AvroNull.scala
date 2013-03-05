package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroNull extends AvroType[Unit] {

  def write(obj: Unit): Array[Byte] = ???

  def read(bytes: Array[Byte]): Unit = ???

}
