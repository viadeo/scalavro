package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroNamedType
import scala.util.Try

class AvroEnum[T] extends AvroNamedType[T] {

  val typeName = "enum"

  def write(obj: T): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[T]
  }

  override def schema() = ???

}

