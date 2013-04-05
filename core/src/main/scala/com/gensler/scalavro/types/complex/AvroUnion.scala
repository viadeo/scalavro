package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroComplexType}
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.JsonSchemaProtocol._

import scala.reflect.runtime.universe._
import scala.util.Success

import spray.json._

class AvroUnion[A: TypeTag, B: TypeTag] extends AvroComplexType[Either[A, B]] {

  val leftType: AvroType[A] = AvroType.fromType[A].get
  val rightType: AvroType[B] = AvroType.fromType[B].get

  val typeName = "union"

  def schema() =
    Set(
      leftType.schema,
      rightType.schema
    ).toJson

  def selfContainedSchema(
    resolvedSymbols: scala.collection.mutable.Set[String] = scala.collection.mutable.Set[String]()
  ) = Set(
        selfContainedSchemaOrFullyQualifiedName(leftType, resolvedSymbols),
        selfContainedSchemaOrFullyQualifiedName(rightType, resolvedSymbols)
      ).toJson

  override def parsingCanonicalForm(): JsValue =
    Set(
      leftType.canonicalFormOrFullyQualifiedName,
      rightType.canonicalFormOrFullyQualifiedName
    ).toJson

  def dependsOn(thatType: AvroType[_]) =
    leftType == thatType || rightType == thatType ||
    (leftType dependsOn thatType) || (rightType dependsOn thatType)

}
