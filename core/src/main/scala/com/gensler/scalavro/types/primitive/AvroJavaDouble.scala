package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroNullablePrimitiveType

/**
  * Represents a mapping from java.lang.Double to the corresponding Avro type.
  */
trait AvroJavaDouble extends AvroNullablePrimitiveType[java.lang.Double] {
  val typeName = "double"
}

object AvroJavaDouble extends AvroJavaDouble