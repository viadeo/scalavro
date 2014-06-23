package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroNullablePrimitiveType

/**
  * Represents a mapping from java.lang.Boolean to the corresponding Avro type.
  */
trait AvroJavaBoolean extends AvroNullablePrimitiveType[java.lang.Boolean] {
  val typeName = "boolean"
}

object AvroJavaBoolean extends AvroJavaBoolean