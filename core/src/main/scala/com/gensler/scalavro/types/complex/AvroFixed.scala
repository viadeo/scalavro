package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroNamedType }
import com.gensler.scalavro.JsonSchemaProtocol._

import com.gensler.scalavro.util.FixedData

import spray.json._

import scala.reflect.runtime.universe._
import scala.collection.immutable.ListMap

class AvroFixed[T <: FixedData: TypeTag](
    val name: String,
    val size: Int,
    val namespace: Option[String] = None,
    val aliases: Seq[String] = Seq()) extends AvroNamedType[T] {

  val typeName = "fixed"

  def schema() = {
    val requiredParams = ListMap(
      "name" -> name.toJson,
      "type" -> typeName.toJson,
      "size" -> size.toJson
    )

    val namespaceParam = ListMap(
      "namespace" -> namespace
    ).collect { case (k, Some(v)) => (k, v.toJson) }

    val aliasesParam = ListMap(
      "aliases" -> aliases
    ).collect { case (k, v) => (k, v.toJson) }

    new JsObject(requiredParams ++ namespaceParam ++ aliasesParam)
  }

  def selfContainedSchema(
    resolvedSymbols: scala.collection.mutable.Set[String] = scala.collection.mutable.Set[String]()) = schema

  override def parsingCanonicalForm(): JsValue = fullyQualify(withoutDocOrAliases(schema))

}
