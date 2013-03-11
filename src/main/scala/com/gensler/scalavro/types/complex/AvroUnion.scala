package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroNamedType}
import com.gensler.scalavro.types.primitive.AvroNull
import scala.reflect.runtime.universe._
import scala.util.Try
import spray.json._

class AvroUnion[A: TypeTag, B: TypeTag] extends AvroNamedType[Either[A, B]] {

  val typeName = "union"

  def write(obj: Either[A, B]): Seq[Byte] = obj match {
    case Left(a)  => ???
    case Right(b) => ???
  }

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[Either[A, B]]
  }

  override def schema() = Set(
    AvroType.fromTypeTag[A].toOption.getOrElse(AvroNull).typeName,
    AvroType.fromTypeTag[B].toOption.getOrElse(AvroNull).typeName
  ).toJson

}
