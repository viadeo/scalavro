package com.gensler.scalavro.tests

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.error._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class AvroBooleanSpec extends AvroSpec {

  val ab = AvroBoolean

  "AvroBoolean" should "be a subclass of AvroType[Boolean]" in {
    ab.isInstanceOf[AvroType[Boolean]] should be (true)
    typeOf[ab.scalaType] =:= typeOf[Boolean] should be (true)
  }

  it should "be a primitive AvroType" in {
    ab.isPrimitive should be (true)
  }

  it should "write Booleans to a stream" in {
    val out = new ByteArrayOutputStream

    ab.write(true, out)
    ab.write(false, out)

    val bytes = out.toByteArray
    bytes.toSeq should equal (Seq(1.toByte, 0.toByte))

    val in = new ByteArrayInputStream(bytes)

    ab read in should equal (Success(true))
    ab read in should equal (Success(false))
  }

  it should "read Booleans from a stream" in {
    val trueStream = new ByteArrayInputStream(Array(1.toByte))
    val falseStream = new ByteArrayInputStream(Array(0.toByte))
    val errorStream = new ByteArrayInputStream(Array(61.toByte))

    ab read trueStream should equal (Success(true))
    ab read falseStream should equal (Success(false))

    evaluating { ab.read(errorStream).get } should produce [AvroDeserializationException[Boolean]]
  }

}