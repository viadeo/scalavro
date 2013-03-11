package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import scala.util.Try

object AvroFloat extends AvroPrimitiveType[Float] {

  val typeName = "float"

  def write(value: Float): Seq[Byte] = {
    val bits: Int = java.lang.Float.floatToIntBits(value)
    Seq(
      bits.toByte,
      (bits >> 8).toByte,
      (bits >> 16).toByte,
      (bits >> 24).toByte
    )
  }

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[Float]
  }

}
