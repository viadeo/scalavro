package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType

object AvroBoolean extends AvroType[Boolean] {

  val typeName = "boolean"

  /**
    * a boolean is written as a single byte whose value is either 0 (false) or
    * 1 (true).
    */
  def write(value: Boolean): Seq[Byte] = 
    if (value) Seq(1.toByte)
    else Seq(0.toByte)

  def read(bytes: Seq[Byte]): Boolean = ???

}
