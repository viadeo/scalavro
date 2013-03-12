package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroNamedType
import scala.reflect.runtime.universe._
import scala.util.Try

class AvroEnum[T: TypeTag] extends AvroNamedType[T] {

  val typeName = "enum"

  def write(obj: T): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[T]
  }

  override def schema() = ???

}

