package com.gensler.scalavro.tests

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.error._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class AvroNullSpec extends AvroSpec {

  val aType = AvroNull

  "AvroNull" should "be a subclass of AvroType[Unit]" in {
    aType.isInstanceOf[AvroType[Unit]] should be (true)
    typeOf[aType.scalaType] =:= typeOf[Unit] should be (true)
  }

  it should "be a primitive AvroType" in {
    aType.isPrimitive should be (true)
  }

  it should "read and write Units" in {
    val out = new ByteArrayOutputStream

    aType.write((), out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    aType read in should equal (Success(()))
  }

}