package com.gensler.scalavro.tests

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.error._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class AvroFloatSpec extends AvroSpec {

  val af = AvroFloat

  "AvroFloat" should "be a subclass of AvroType[Float]" in {
    af.isInstanceOf[AvroType[Float]] should be (true)
    typeOf[af.scalaType] =:= typeOf[Float] should be (true)
  }

  it should "be a primitive AvroType" in {
    af.isPrimitive should be (true)
  }

  it should "read and write Floats" in {
    val out = new ByteArrayOutputStream

    af.write(5.3F, out)
    af.write(-88.421F, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    af read in should equal (Success(5.3F))
    af read in should equal (Success(-88.421F))
  }

}