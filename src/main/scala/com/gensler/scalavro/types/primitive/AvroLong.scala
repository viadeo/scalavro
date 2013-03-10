package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroType
import scala.util.Try

object AvroLong extends AvroType[Long] {

  val typeName = "long"

  def write(obj: Long): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[Long]
  }

}
