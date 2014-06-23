package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroJavaByteSpec extends AvroSpec {

  val ab = AvroJavaByte

  "AvroJavaByte" should "be a subclass of AvroType[java.lang.Byte]" in {
    ab.isInstanceOf[AvroType[java.lang.Byte]] should be (true)
    typeOf[ab.scalaType] =:= typeOf[java.lang.Byte] should be (true)
  }

  it should "be a primitive AvroType" in {
    ab.isPrimitive should be (true)
  }

}