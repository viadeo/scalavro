package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroJavaLongSpec extends AvroSpec {

  val al = AvroJavaLong

  "AvroJavaLong" should "be a subclass of AvroType[java.lang.Long]" in {
    al.isInstanceOf[AvroType[java.lang.Long]] should be (true)
    typeOf[al.scalaType] =:= typeOf[java.lang.Long] should be (true)
  }

  it should "be a primitive AvroType" in {
    al.isPrimitive should be (true)
  }

}