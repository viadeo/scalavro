package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import scala.util.{Try, Success}

object AvroNull extends AvroPrimitiveType[Unit] {

  val typeName = "null"

  /**
    * null is written as zero bytes.
    */
  def write(obj: Unit): Seq[Byte] = Seq()

  def read(bytes: Seq[Byte]) = Success(())

}
