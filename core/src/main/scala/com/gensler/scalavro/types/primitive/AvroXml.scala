package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroNullablePrimitiveType

/**
  * Represents a mapping from scala.xml.Node to the corresponding Avro type.
  */
trait AvroXml extends AvroNullablePrimitiveType[scala.xml.Node] {
  val typeName = "string"
}

object AvroXml extends AvroXml