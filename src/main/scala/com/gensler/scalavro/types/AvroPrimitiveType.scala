package com.gensler.scalavro.types

import scala.reflect.runtime.universe.TypeTag

trait AvroPrimitiveType[T] extends AvroType[T] {
  final val isPrimitive = true
  final def dependsOn[U](thatType: AvroNamedType[U]) = false
}
