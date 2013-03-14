package com.gensler.scalavro.types

/**
  * Marker trait for "named types".  As of version 1.7.4 of the Avro
  * specification, the named types are `Record`, `Union`, and `Fixed`.
  */
trait AvroNamedType[T] extends AvroComplexType[T] {
  def name: String
}
