package com.gensler.scalavro.tests

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.error._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class AvroRecordSpec extends AvroSpec {

  val personRecord = AvroType.fromType[Person].get

  "AvroRecord" should "be parameterized with its corresponding Scala type" in {
    personRecord.isInstanceOf[AvroType[Person]] should be (true)
    typeOf[personRecord.scalaType] =:= typeOf[Person] should be (true)
  }

  it should "be a complex AvroType" in {
    personRecord.isPrimitive should be (false)
  }

  it should "write and read values to a to/from a stream" in {
    val out = new ByteArrayOutputStream

    val julius = Person("Julius Caesar", 2112)

    personRecord.write(julius, out)

    val bytes = out.toByteArray
    // bytes.toSeq should equal (...)

    val in = new ByteArrayInputStream(bytes)

    personRecord read in should equal (julius)
  }

}