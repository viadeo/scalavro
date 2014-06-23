package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.collection.mutable
import scala.reflect.runtime.universe.TypeTag

trait AvroNullablePrimitiveTypeIO[T] extends AvroPrimitiveTypeIO[T] {

  protected val UNION_INDEX_NULL: Long = 0
  protected val UNION_INDEX_VALUE: Long = 1

}
