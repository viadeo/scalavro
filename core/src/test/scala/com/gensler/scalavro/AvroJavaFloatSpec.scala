package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroJavaFloatSpec extends AvroSpec {

  val af = AvroJavaFloat

  "AvroJavaFloat" should "be a subclass of AvroType[java.lang.Float]" in {
    af.isInstanceOf[AvroType[java.lang.Float]] should be (true)
    typeOf[af.scalaType] =:= typeOf[java.lang.Float] should be (true)
  }

  it should "be a primitive AvroType" in {
    af.isPrimitive should be (true)
  }

}