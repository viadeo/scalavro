package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroComplexType}
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.JsonSchemaProtocol._
import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure}
import spray.json._

class AvroMap[T: TypeTag] extends AvroComplexType[Map[String, T]] {

  type ItemType = T

  val typeName = "map"

  def write(obj: Map[String, T]): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[Map[String, T]]
  }

  override def schema() = Map(
    "type"   -> typeName.toJson,
    "values" -> typeSchemaOrNull[T]
  ).toJson
}
