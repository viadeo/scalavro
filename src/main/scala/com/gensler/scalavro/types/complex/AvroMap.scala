package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroType

class AvroMap[T] extends AvroType[Map[String, T]] {

  val typeName = "map"

  def write(obj: Map[String, T]): Seq[Byte] = ???

  def read(bytes: Seq[Byte]): Map[String, T] = ???

}
