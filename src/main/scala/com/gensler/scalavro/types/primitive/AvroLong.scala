package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroLong extends AvroType[Long] {

  def write(obj: Long): Array[Byte] = ???

  def read(bytes: Array[Byte]): Long = ???

}
