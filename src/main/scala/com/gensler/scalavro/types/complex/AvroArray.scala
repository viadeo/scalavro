package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.primitive.AvroNull
import scala.reflect.runtime.universe._
import scala.util.Try
import spray.json._

class AvroArray[T: TypeTag] extends AvroType[Seq[T]] {

  val typeName = "array"

  def write(obj: Seq[T]): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[Seq[T]]
  }

  override def schema() = { 

    println("Array of [%s]" format typeOf[T])

    Map(
      "type"  -> typeName,
      "items" -> AvroType.fromType[T].toOption.getOrElse(AvroNull).typeName
    ).toJson
  }

}
