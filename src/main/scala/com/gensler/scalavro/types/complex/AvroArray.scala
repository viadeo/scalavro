package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroComplexType}
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.JsonSchemaProtocol._
import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure}
import spray.json._

class AvroArray[T: TypeTag] extends AvroComplexType[Seq[T]] {

  type ItemType = T

  val typeName = "array"

  def write(obj: Seq[T]): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try { ???.asInstanceOf[Seq[T]] }

  override def schema() = Map(
    "type"  -> typeName.toJson,
    "items" -> typeSchemaOrNull[T]
  ).toJson

  def dependsOn(thatType: AvroType[_]) = AvroType.fromType[ItemType] match {
    case Success(avroTypeOfItems) => {
      avroTypeOfItems == thatType || (avroTypeOfItems dependsOn thatType)
    }
    case _ => false
  }

  def parsingCanonicalForm() = ???
}
