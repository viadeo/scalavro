package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroBoolean extends AvroType[Boolean] {

  val typeName = "boolean"

  def write(obj: Boolean): Array[Byte] = ???

  def read(bytes: Array[Byte]): Boolean = ???

}
