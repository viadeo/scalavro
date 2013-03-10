package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.primitive.AvroNull
import scala.util.Try
import spray.json._

class AvroMap[T] extends AvroType[Map[String, T]] {

  val typeName = "map"

  def write(obj: Map[String, T]): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[Map[String, T]]
  }

  override def schema() = Map(
    "type"  -> typeName,
    "values" -> AvroType.fromType[this.scalaType].toOption.getOrElse(AvroNull).typeName
  ).toJson

}
