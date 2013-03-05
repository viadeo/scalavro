package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

case class AvroBoolean(value: Boolean) extends AvroType[Boolean] {

  def write(obj: Boolean): Array[Byte] = ???

  def read(bytes: Array[Byte]): Boolean = ???

}
