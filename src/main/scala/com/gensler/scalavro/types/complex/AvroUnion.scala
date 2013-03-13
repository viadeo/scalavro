package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroNamedType}
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.JsonSchemaProtocol._
import scala.reflect.runtime.universe._
import scala.util.Try
import spray.json._

class AvroUnion[A: TypeTag, B: TypeTag] extends AvroNamedType[Either[A, B]] {

  type LeftType = A
  type RightType = B

  val typeName = "union"

  def write(obj: Either[LeftType, RightType]): Seq[Byte] = obj match {
    case Left(a)  => ???
    case Right(b) => ???
  }

  def read(bytes: Seq[Byte]) = Try { ???.asInstanceOf[Either[A, B]] }

  override def schema() = Set(
    typeSchemaOrNull[LeftType],
    typeSchemaOrNull[RightType]
  ).toJson

}
