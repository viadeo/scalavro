package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroNamedType
import scala.util.Try

class AvroError[T](
  name: String,
  namespace: String,
  fields: Seq[AvroRecord.Field[_]],
  aliases: Seq[String] = Seq(),
  doc: Option[String] = None
) extends AvroRecord[T](
  name,
  namespace,
  fields,
  aliases,
  doc
) {
  override val typeName = "error"
}
