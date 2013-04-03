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

}