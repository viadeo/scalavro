package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroDouble extends AvroType[Double] {

  val typeName = "double"

  def write(obj: Double): Array[Byte] = ???

  def read(bytes: Array[Byte]): Double = ???

}
