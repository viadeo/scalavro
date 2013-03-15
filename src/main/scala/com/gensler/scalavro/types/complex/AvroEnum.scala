package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroNamedType}
import scala.reflect.runtime.universe._
import scala.util.{Try, Success, Failure}

class AvroEnum[T: TypeTag](
  val name: String
) extends AvroNamedType[T] {

  val typeName = "enum"

  def write(obj: T): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try { ???.asInstanceOf[T] }

  override def schema() = ???

  def dependsOn(thatType: AvroType[_]) = AvroType.fromType[T] match {
    case Success(containedType) => {
      containedType == thatType || (containedType dependsOn thatType)
    }
    case _ => false
  }

  def parsingCanonicalForm() = ???
}

