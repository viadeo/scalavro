package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO

import scala.collection.mutable
import scala.reflect.runtime.universe.TypeTag

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

trait AvroPrimitiveTypeIO[T] extends AvroTypeIO[T] {

  protected[scalavro] def write[V <: T: TypeTag](
    value: V,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    write(value, encoder)
  }

  protected[scalavro] def write(value: T, encoder: BinaryEncoder): Unit

  protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = read(decoder)

  protected[scalavro] def read(decoder: BinaryDecoder): T

}
