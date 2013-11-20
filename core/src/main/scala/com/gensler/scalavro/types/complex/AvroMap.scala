package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroComplexType }
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.collection.immutable.ListMap
import scala.collection.mutable
import scala.util.Success

/**
  * Represents a mapping from a Scala type (a subclass of Map[String, _]) to a
  * corresponding Avro type.
  */
class AvroMap[T, M <: Map[String, T]](
  implicit val itemTypeTag: TypeTag[T],
  implicit val originalTypeTag: TypeTag[M])
    extends AvroComplexType[M] {

  val itemType = AvroType.fromType[T].get

  val typeName = "map"

  def selfContainedSchema(
    resolvedSymbols: mutable.Set[String] = mutable.Set[String]()) = new JsObject(ListMap(
    "type" -> typeName.toJson,
    "values" -> selfContainedSchemaOrFullyQualifiedName(itemType, resolvedSymbols)
  ))

}
