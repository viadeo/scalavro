package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroNamedType}
import com.gensler.scalavro.JsonSchemaProtocol._
import scala.reflect.runtime.universe._
import scala.util.Try
import spray.json._

class AvroFixed[T: TypeTag](
  val name: String,
  val size: Int,
  val namespace: Option[String] = None,
  val aliases: Seq[String] = Seq()
) extends AvroNamedType[T] {

  val typeName = "fixed"

  def write(obj: T): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try { ???.asInstanceOf[T] }

  override def schema() = {
    val requiredParams = Map(
      "type" -> typeName.toJson,
      "name" -> name.toJson,
      "size" -> size.toJson
    )

    val namespaceParam = Map(
      "namespace" -> namespace
    ).collect { case (k, Some(v)) => (k, v.toJson) }

    val aliasesParam = Map(
      "aliases" -> aliases
    ).collect { case (k, v) => (k, v.toJson) }

    (requiredParams ++ namespaceParam ++ aliasesParam).toJson
  }

  def dependsOn(thatType: AvroType[_]) = false

  def parsingCanonicalForm() = schema
}
