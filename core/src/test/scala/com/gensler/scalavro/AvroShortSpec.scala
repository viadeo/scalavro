package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroShortSpec extends AvroSpec {

  val as = AvroShort

  "AvroShort" should "be a subclass of AvroType[Short]" in {
    as.isInstanceOf[AvroType[Short]] should be (true)
    typeOf[as.scalaType] =:= typeOf[Short] should be (true)
  }

  it should "be a primitive AvroType" in {
    as.isPrimitive should be (true)
  }

}