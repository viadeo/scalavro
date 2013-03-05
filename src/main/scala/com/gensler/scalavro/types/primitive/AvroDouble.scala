package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroDouble extends AvroType[Double] {

  def write(obj: Double): Array[Byte] = ???

  def read(bytes: Array[Byte]): Double = ???

}
