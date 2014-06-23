package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroNullablePrimitiveType

/**
  * Represents a mapping from java.lang.Integer to the corresponding Avro type.
  */
trait AvroJavaInteger extends AvroNullablePrimitiveType[java.lang.Integer] {
  val typeName = "int"
}

object AvroJavaInteger extends AvroJavaInteger