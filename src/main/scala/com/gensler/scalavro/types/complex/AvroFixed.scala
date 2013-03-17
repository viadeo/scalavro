package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroNamedType}
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure }
import scala.collection.immutable.ListMap

class AvroFixed[T: TypeTag](
  val name: String,
  val size: Int,
  val namespace: Option[String] = None,
  val aliases: Seq[String] = Seq()
) extends AvroNamedType[T] {

  val typeName = "fixed"

  def write(obj: T): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try { ???.asInstanceOf[T] }

  // name, type, fields, symbols, items, values, size
  override def schema() = {
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

    (requiredParams ++ namespaceParam ++ aliasesParam).toJson
  }

  override def parsingCanonicalForm(): JsValue = fullyQualify(withoutDocOrAliases(schema))

  def dependsOn(thatType: AvroType[_]) = false

}
