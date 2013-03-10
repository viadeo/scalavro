package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType
import scala.util.Try

object AvroBytes extends AvroType[Seq[Byte]] {

  val typeName = "bytes"

  def write(obj: Seq[Byte]): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[Seq[Byte]]
  }

}
