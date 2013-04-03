package com.gensler.scalavro.types

import scala.reflect.runtime.universe._

abstract class AvroPrimitiveType[T: TypeTag] extends AvroType[T] {
  final val isPrimitive = true
  final def dependsOn(thatType: AvroType[_]) = false
}
