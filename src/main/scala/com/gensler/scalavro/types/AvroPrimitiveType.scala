package com.gensler.scalavro.types

trait AvroPrimitiveType[T] extends AvroType[T] {
  final val isPrimitive = true
}
