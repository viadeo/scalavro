package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroNamedType

class AvroError[T](
  name: String,
  namespace: String,
  fields: Seq[AvroRecordField[_]],
  aliases: Seq[String] = Seq(),
  doc: Option[String] = None
) extends AvroNamedType[T] {

  val typeName = "error"

  def write(obj: T): Seq[Byte] = ???

  def read(bytes: Seq[Byte]): T = ???

}
