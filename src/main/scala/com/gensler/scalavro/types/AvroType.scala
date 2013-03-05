package com.gensler.scalavro.types

import com.gensler.scalavro.types.primitive.AvroNull

import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure}

trait AvroType[T] {
  type scalaType = T

  def write(obj: T): Array[Byte]

  def read(bytes: Array[Byte]): T

}

object AvroType {

  def fromType[T](implicit tt: TypeTag[T]): Try[AvroType[T]] = Try {
    ???
  }

}