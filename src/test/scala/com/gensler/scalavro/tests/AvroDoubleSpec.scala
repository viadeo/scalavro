package com.gensler.scalavro.tests

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.error._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class AvroDoubleSpec extends AvroSpec {

  val ad = AvroDouble

  "AvroDouble" should "be a subclass of AvroType[Double]" in {
    ad.isInstanceOf[AvroType[Double]] should be (true)
    typeOf[ad.scalaType] =:= typeOf[Double] should be (true)
  }

  it should "be a primitive AvroType" in {
    ad.isPrimitive should be (true)
  }

  it should "read and write Doubles" in {
    val out = new ByteArrayOutputStream

    ad.write(math.Pi, out)
    ad.write(1.23, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    ad read in should equal (Success(math.Pi))
    ad read in should equal (Success(1.23))
  }

}