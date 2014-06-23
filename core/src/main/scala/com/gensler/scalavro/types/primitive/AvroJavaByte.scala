package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroNullablePrimitiveType

/**
  * Represents a mapping from java.lang.Byte to the corresponding Avro type.
  */
trait AvroJavaByte extends AvroNullablePrimitiveType[java.lang.Byte] {
  val typeName = "int"
}

object AvroJavaByte extends AvroJavaByte