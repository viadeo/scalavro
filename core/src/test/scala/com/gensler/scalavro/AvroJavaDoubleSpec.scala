package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroJavaDoubleSpec extends AvroSpec {

  val ad = AvroJavaDouble

  "AvroJavaDouble" should "be a subclass of AvroType[java.lang.Double]" in {
    ad.isInstanceOf[AvroType[java.lang.Double]] should be (true)
    typeOf[ad.scalaType] =:= typeOf[java.lang.Double] should be (true)
  }

  it should "be a primitive AvroType" in {
    ad.isPrimitive should be (true)
  }

}