package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import scala.util.Try

object AvroDouble extends AvroPrimitiveType[Double] {

  val typeName = "double"

  def write(value: Double): Seq[Byte] = {
    val bits: Long = java.lang.Double.doubleToLongBits(value)
    Seq(
      bits.toByte,
      (bits >> 8).toByte,
      (bits >> 16).toByte,
      (bits >> 24).toByte,
      (bits >> 32).toByte,
      (bits >> 40).toByte,
      (bits >> 48).toByte,
      (bits >> 56).toByte
    )
  }

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[Double]
  }

}
