package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroJavaIntegerSpec extends AvroSpec {

  val ai = AvroJavaInteger

  "AvroInt" should "be a subclass of AvroType[java.lang.Integer]" in {
    ai.isInstanceOf[AvroType[java.lang.Integer]] should be (true)
    typeOf[ai.scalaType] =:= typeOf[java.lang.Integer] should be (true)
  }

  it should "be a primitive AvroType" in {
    ai.isPrimitive should be (true)
  }

}