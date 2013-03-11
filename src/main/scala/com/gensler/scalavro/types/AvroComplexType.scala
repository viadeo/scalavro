package com.gensler.scalavro.types

trait AvroComplexType[T] extends AvroType[T] {
  final val isPrimitive = false
}
