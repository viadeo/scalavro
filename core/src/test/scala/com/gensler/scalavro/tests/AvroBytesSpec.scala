package com.gensler.scalavro.tests

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.error._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class AvroBytesSpec extends AvroSpec {

  lazy val ab = AvroType.fromType[Seq[Byte]].get.asInstanceOf[AvroBytes]

  "AvroBytes" should "be a subclass of AvroType[Seq[Byte]]" in {
    ab == AvroBytes should be (true)
    ab.isInstanceOf[AvroBytes] should be (true)
    typeOf[ab.scalaType] =:= typeOf[Seq[Byte]] should be (true)
  }

  it should "be a primitive AvroType" in {
    ab.isPrimitive should be (true)
  }

  it should "read and write bytes" in {
    val text = "The quick brown fox jumped over the lazy dog."
    val out = new ByteArrayOutputStream
    ab.write(text.getBytes, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)
    ab.read(in).get.toSeq should equal (text.getBytes.toSeq)
  }

}