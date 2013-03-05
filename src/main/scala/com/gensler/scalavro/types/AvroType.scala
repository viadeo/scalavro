package com.gensler.scalavro.types

import com.gensler.scalavro.types.primitive._

import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure}

trait AvroType[T] {

  type scalaType = T

  def typeName(): String

  def write(obj: T): Seq[Byte]

  def read(bytes: Seq[Byte]): T

}

object AvroType {

  val primitiveTags: Map[TypeTag[_], AvroType[_]] = Map(
    typeTag[Unit]      -> AvroNull,
    typeTag[Boolean]   -> AvroBoolean,
    typeTag[Seq[Byte]] -> AvroBytes,
    typeTag[Double]    -> AvroDouble,
    typeTag[Float]     -> AvroFloat,
    typeTag[Int]       -> AvroInt,
    typeTag[Long]      -> AvroLong,
    typeTag[String]    -> AvroString
  )

  def fromType[T](implicit tt: TypeTag[T]): Try[AvroType[T]] =
    Try {
      primitiveTags.get(tt) match {
        case Some(primitive) => primitive.asInstanceOf[AvroType[T]]
        case None            => ??? // complex types not handled yet
      }
    }

}