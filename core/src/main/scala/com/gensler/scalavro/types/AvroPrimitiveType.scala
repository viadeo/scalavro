package com.gensler.scalavro.types

import scala.reflect.runtime.universe._
import spray.json._
import spray.json.DefaultJsonProtocol._

/**
  * Parent class of all simple Avro types.
  */
abstract class AvroPrimitiveType[T: TypeTag] extends AvroType[T] {

  final val isPrimitive = true

  final def dependsOn(thatType: AvroType[_]) = false

  def schema(): spray.json.JsValue = typeName.toJson

  def selfContainedSchema(
    resolvedSymbols: scala.collection.mutable.Set[String] = scala.collection.mutable.Set[String]()) = schema

}
