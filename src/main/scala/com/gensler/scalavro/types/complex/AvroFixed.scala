package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroNamedType

class AvroFixed[T](
  name: String,
  size: Int,
  namespace: Option[String] = None,
  aliases: Seq[String] = Seq()
) extends AvroNamedType[T] {

  val typeName = "fixed"

  def write(obj: T): Seq[Byte] = ???

  def read(bytes: Seq[Byte]): T = ???

}
