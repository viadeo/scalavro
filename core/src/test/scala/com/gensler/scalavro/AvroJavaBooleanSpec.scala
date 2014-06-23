package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroJavaBooleanSpec extends AvroSpec {

  val ab = AvroJavaBoolean

  "AvroJavaBoolean" should "be a subclass of AvroType[java.lang.Boolean]" in {
    ab.isInstanceOf[AvroType[java.lang.Boolean]] should be (true)
    typeOf[ab.scalaType] =:= typeOf[java.lang.Boolean] should be (true)
  }

  it should "be a primitive AvroType" in {
    ab.isPrimitive should be (true)
  }

}