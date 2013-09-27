package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroNamedType }
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.collection.immutable.ListMap
import scala.collection.mutable
import scala.util.Success

class AvroEnum[E <: Enumeration: TypeTag](
    val name: String,
    val symbols: Seq[String],
    val namespace: Option[String] = None) extends AvroNamedType[E#Value] {

  val enumTag = typeTag[E]

  val typeName = "enum"

  def selfContainedSchema(
    resolvedSymbols: mutable.Set[String] = mutable.Set[String]()) = {

    val requiredParams = ListMap(
      "name" -> name.toJson,
      "type" -> typeName.toJson,
      "symbols" -> symbols.toJson
    )

    val optionalParams = ListMap(
      "namespace" -> namespace
    ).collect { case (k, Some(v)) => (k, v.toJson) }

    new JsObject(requiredParams ++ optionalParams)
  }

  override def parsingCanonicalForm(): JsValue = fullyQualify(schema)

}
