package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroNamedType}
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure}
import scala.collection.immutable.ListMap

class AvroEnum[T: TypeTag](
  val name: String,
  val namespace: Option[String] = None
) extends AvroNamedType[T] {

  val typeName = "enum"

  def write(obj: T): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try { ???.asInstanceOf[T] }

  // name, type, fields, symbols, items, values, size
  override def schema() = {
    val requiredParams = ListMap(
      "name"    -> name.toJson,
      "type"    -> typeName.toJson,
      "symbols" -> Seq(1, 2).toJson // TODO: populate symbols!
    )

    val optionalParams = ListMap(
      "namespace" -> namespace
    ).collect { case (k, Some(v)) => (k, v.toJson) }

    (requiredParams ++ optionalParams).toJson
  }

  override def parsingCanonicalForm(): JsValue = fullyQualify(schema)

  def dependsOn(thatType: AvroType[_]) = AvroType.fromType[T] match {
    case Success(containedType) => {
      containedType == thatType || (containedType dependsOn thatType)
    }
    case _ => false
  }

}

