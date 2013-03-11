package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import scala.util.Try

object AvroInt extends AvroPrimitiveType[Int] {

  val typeName = "int"

  def write(obj: Int): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[Int]
  }

}
