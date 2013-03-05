package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroNamedType

class AvroError[T](
  name: String,
  namespace: String,
  fields: Seq[AvroRecordField[_]],
  aliases: Seq[String] = Seq(),
  doc: Option[String] = None
) extends AvroNamedType[T] {

  def write(obj: T): Array[Byte] = ???

  def read(bytes: Array[Byte]): T = ???

}
