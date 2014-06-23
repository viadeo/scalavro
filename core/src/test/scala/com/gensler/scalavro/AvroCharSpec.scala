package com.gensler.scalavro.test

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

class AvroCharSpec extends AvroSpec {

  val ab = AvroChar

  "AvroByte" should "be a subclass of AvroType[Char]" in {
    ab.isInstanceOf[AvroType[Char]] should be (true)
    typeOf[ab.scalaType] =:= typeOf[Char] should be (true)
  }

  it should "be a primitive AvroType" in {
    ab.isPrimitive should be (true)
  }

}