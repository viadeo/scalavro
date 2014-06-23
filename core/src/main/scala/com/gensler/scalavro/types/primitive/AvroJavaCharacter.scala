package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroNullablePrimitiveType

/**
  * Represents a mapping from java.lang.Character to the corresponding Avro type.
  */
trait AvroJavaCharacter extends AvroNullablePrimitiveType[java.lang.Character] {
  val typeName = "int"
}

object AvroJavaCharacter extends AvroJavaCharacter