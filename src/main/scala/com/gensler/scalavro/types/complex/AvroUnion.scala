package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.AvroNamedType

class AvroUnion[A, B] extends AvroNamedType[Either[A, B]] {

  val typeName = "union"

  def write(obj: Either[A, B]): Array[Byte] = obj match {
    case Left(a)  => ???
    case Right(b) => ???
  }

  def read(bytes: Array[Byte]): Either[A, B] = ???

}
