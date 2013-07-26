package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroComplexType, AvroNamedType }
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.collection.immutable.ListMap
import scala.util.Success

class AvroArray[T, S <: Seq[T]](
  implicit val itemTypeTag: TypeTag[T],
  implicit val originalTypeTag: TypeTag[S])
    extends AvroComplexType[S] {

  val itemType = AvroType.fromType[T].get

  val typeName = "array"

  // name, type, fields, symbols, items, values, size
  def schema() = new JsObject(ListMap(
    "type" -> typeName.toJson,
    "items" -> itemType.schema
  ))

  def selfContainedSchema(
    resolvedSymbols: scala.collection.mutable.Set[String] = scala.collection.mutable.Set[String]()) = new JsObject(ListMap(
    "type" -> typeName.toJson,
    "items" -> selfContainedSchemaOrFullyQualifiedName(itemType, resolvedSymbols)
  ))

  override def parsingCanonicalForm(): JsValue = new JsObject(ListMap(
    "type" -> typeName.toJson,
    "items" -> itemType.canonicalFormOrFullyQualifiedName
  ))

  def dependsOn(thatType: AvroType[_]) =
    itemType == thatType || (itemType dependsOn thatType)

}
