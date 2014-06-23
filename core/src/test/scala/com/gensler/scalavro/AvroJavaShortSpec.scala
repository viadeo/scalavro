package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroJavaShortSpec extends AvroSpec {

  val al = AvroJavaShort

  "AvroJavaShort" should "be a subclass of AvroType[java.lang.Short]" in {
    al.isInstanceOf[AvroType[java.lang.Short]] should be (true)
    typeOf[al.scalaType] =:= typeOf[java.lang.Short] should be (true)
  }

  it should "be a primitive AvroType" in {
    al.isPrimitive should be (true)
  }

}