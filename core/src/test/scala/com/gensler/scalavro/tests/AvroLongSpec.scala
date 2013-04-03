package com.gensler.scalavro.tests

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.error._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class AvroLongSpec extends AvroSpec {

  val al = AvroLong

  "AvroLong" should "be a subclass of AvroType[Long]" in {
    al.isInstanceOf[AvroType[Long]] should be (true)
    typeOf[al.scalaType] =:= typeOf[Long] should be (true)
  }

  it should "be a primitive AvroType" in {
    al.isPrimitive should be (true)
  }

  it should "read and write Longs" in {
    val out = new ByteArrayOutputStream

    al.write(55L, out)
    al.write(8675309L, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    al read in should equal (Success(55L))
    al read in should equal (Success(8675309L))
  }

}