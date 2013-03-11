package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import scala.util.Try

object AvroBytes extends AvroPrimitiveType[Seq[Byte]] {

  val typeName = "bytes"

  def write(obj: Seq[Byte]): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[Seq[Byte]]
  }

}
