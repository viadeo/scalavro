package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroNamedType }
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.collection.immutable.ListMap

class AvroFixed(
    val name: String,
    val size: Int,
    val namespace: Option[String] = None,
    val aliases: Seq[String] = Seq()) extends AvroNamedType[Seq[Byte]] {

  val typeName = "fixed"

  def schema() = {
    val requiredParams = ListMap(
      "name" -> name.toJson,
      "type" -> typeName.toJson,
      "size" -> size.toJson)

    val namespaceParam = ListMap(
      "namespace" -> namespace).collect { case (k, Some(v)) => (k, v.toJson) }

    val aliasesParam = ListMap(
      "aliases" -> aliases).collect { case (k, v) => (k, v.toJson) }

    new JsObject(requiredParams ++ namespaceParam ++ aliasesParam)
  }

  def selfContainedSchema(
    resolvedSymbols: scala.collection.mutable.Set[String] = scala.collection.mutable.Set[String]()) = schema

  override def parsingCanonicalForm(): JsValue = fullyQualify(withoutDocOrAliases(schema))

  def dependsOn(thatType: AvroType[_]) = false

}
