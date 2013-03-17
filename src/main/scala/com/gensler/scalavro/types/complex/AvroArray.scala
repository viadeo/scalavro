package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroComplexType}
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure}
import scala.collection.immutable.ListMap

class AvroArray[T: TypeTag] extends AvroComplexType[Seq[T]] {

  type ItemType = T

  val typeName = "array"

  def write(obj: Seq[T]): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try { ???.asInstanceOf[Seq[T]] }

  // name, type, fields, symbols, items, values, size
  override def schema() = ListMap(
    "type"  -> typeName.toJson,
    "items" -> typeSchemaOrNull[T]
  ).toJson

  override def parsingCanonicalForm(): JsValue = ListMap(
    "type"  -> typeName.toJson,
    "items" -> {
      AvroType.fromType[ItemType].map { _.canonicalFormOrFullyQualifiedName } getOrElse AvroNull.schema
    }
  ).toJson

  def dependsOn(thatType: AvroType[_]) = AvroType.fromType[ItemType] match {
    case Success(avroTypeOfItems) => {
      avroTypeOfItems == thatType || (avroTypeOfItems dependsOn thatType)
    }
    case _ => false
  }

}
