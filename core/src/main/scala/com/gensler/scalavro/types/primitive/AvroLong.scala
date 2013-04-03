package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import spray.json._

trait AvroLong extends AvroPrimitiveType[Long] {
  val typeName = "long"
}

object AvroLong extends AvroLong