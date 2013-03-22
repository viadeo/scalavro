package com.gensler.scalavro.tests

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.error._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class AvroStringSpec extends AvroSpec {

  val as = AvroString

  "AvroString" should "be a subclass of AvroType[String]" in {
    as.isInstanceOf[AvroType[String]] should be (true)
    typeOf[as.scalaType] =:= typeOf[String] should be (true)
  }

  it should "be a primitive AvroType" in {
    as.isPrimitive should be (true)
  }

  it should "read and write Strings" in {
    val text = "The quick brown fox jumped over the lazy dog."

    val out = new ByteArrayOutputStream
    as.write(text, out)
    val bytes = out.toByteArray

    println(bytes.length)

    val in = new ByteArrayInputStream(bytes)

    as.read(in).get should equal (text)
  }

}