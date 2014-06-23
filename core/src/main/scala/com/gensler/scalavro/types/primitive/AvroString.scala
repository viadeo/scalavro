package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroNullablePrimitiveType

/**
  * Represents a mapping from java.lang.String to the corresponding Avro type.
  */
trait AvroString extends AvroNullablePrimitiveType[String] {
  val typeName = "string"
}

object AvroString extends AvroString