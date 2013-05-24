package com.gensler.scalavro.tests

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.complex.AvroFixed

import com.gensler.scalavro.util.FixedData

class AvroFixedSpec extends AvroSpec {

  val af = AvroType[MD5].asInstanceOf[AvroFixed[MD5]]

  "AvroFixed" should "be parameterized with its corresponding Scala type" in {
    af.isInstanceOf[AvroFixed[_]] should be (true)
    typeOf[af.scalaType] =:= typeOf[MD5] should be (true)
  }

  it should "be a complex AvroType" in {
    af.isPrimitive should be (false)
  }

  it should "get its name from the source FixedData class" in {
    af.name should be ("MD5")
  }

  it should "get its namespace from the source FixedData class" in {
    af.namespace should be (Some("com.gensler.scalavro.tests"))
  }

  it should "get its size from the source FixedData companion object" in {
    af.size should be (16)
  }

}