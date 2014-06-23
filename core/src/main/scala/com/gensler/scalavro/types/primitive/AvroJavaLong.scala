package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroNullablePrimitiveType

/**
  * Represents a mapping from java.lang.Long to the corresponding Avro type.
  */
trait AvroJavaLong extends AvroNullablePrimitiveType[java.lang.Long] {
  val typeName = "long"
}

object AvroJavaLong extends AvroJavaLong