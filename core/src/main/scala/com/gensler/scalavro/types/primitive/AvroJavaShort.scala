package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroNullablePrimitiveType

/**
  * Represents a mapping from java.lang.Short to the corresponding Avro type.
  */
trait AvroJavaShort extends AvroNullablePrimitiveType[java.lang.Short] {
  val typeName = "int"
}

object AvroJavaShort extends AvroJavaShort