package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroComplexType}
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure}
import scala.collection.immutable.ListMap

import java.io.{InputStream, OutputStream}

class AvroMap[T: TypeTag] extends AvroComplexType[Map[String, T]] {

  type ItemType = T

  val typeName = "map"

  def write(obj: Map[String, T], stream: OutputStream) = ???

  def writeAsJson(obj: Map[String, T]): JsValue = ???

  def read(stream: InputStream) = Try { ???.asInstanceOf[Map[String, T]] }

  def readFromJson(json: JsValue) = Try { ???.asInstanceOf[Map[String, T]] }

  // name, type, fields, symbols, items, values, size
  override def schema() = new JsObject(ListMap(
    "type"   -> typeName.toJson,
    "values" -> typeSchemaOrNull[T]
  ))

  override def parsingCanonicalForm(): JsValue = new JsObject(ListMap(
    "type"   -> typeName.toJson,
    "values" -> {
      AvroType.fromType[ItemType].map { _.canonicalFormOrFullyQualifiedName } getOrElse AvroNull.schema
    }
  ))

  def dependsOn(thatType: AvroType[_]) = AvroType.fromType[ItemType] match {
    case Success(avroTypeOfItems) => {
      avroTypeOfItems == thatType || (avroTypeOfItems dependsOn thatType)
    }
    case _ => false
  }

}
