package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroType

class AvroMap[T] extends AvroType[Map[String, T]] {

  val typeName = "map"

  def write(obj: Map[String, T]): Array[Byte] = ???

  def read(bytes: Array[Byte]): Map[String, T] = ???

}
