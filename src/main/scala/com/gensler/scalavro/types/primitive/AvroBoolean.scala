package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import scala.util.Try
import spray.json._

object AvroBoolean extends AvroPrimitiveType[Boolean] with DefaultJsonProtocol {

  val typeName = "boolean"

  /**
    * a boolean is written as a single byte whose value is either 0 (false) or
    * 1 (true).
    */
  def write(value: Boolean): Seq[Byte] = 
    if (value) Seq(1.toByte)
    else Seq(0.toByte)

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[Boolean]
  }

}
