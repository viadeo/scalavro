package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroNullablePrimitiveType

/**
  * Represents a mapping from java.lang.Float to the corresponding Avro type.
  */
trait AvroJavaFloat extends AvroNullablePrimitiveType[java.lang.Float] {
  val typeName = "float"
}

object AvroJavaFloat extends AvroJavaFloat