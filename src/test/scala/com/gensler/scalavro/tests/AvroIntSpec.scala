package com.gensler.scalavro.tests

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.error._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class AvroIntSpec extends AvroSpec {

  val ai = AvroInt

  "AvroInt" should "be a subclass of AvroType[Int]" in {
    ai.isInstanceOf[AvroType[Int]] should be (true)
    typeOf[ai.scalaType] =:= typeOf[Int] should be (true)
  }

  it should "be a primitive AvroType" in {
    ai.isPrimitive should be (true)
  }

  it should "read and write Ints" in {
    val out = new ByteArrayOutputStream

    ai.write(55, out)
    ai.write(8675309, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    ai read in should equal (Success(55))
    ai read in should equal (Success(8675309))
  }

}