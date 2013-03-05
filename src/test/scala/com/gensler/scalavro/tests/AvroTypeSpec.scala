package com.gensler.scalavro.tests

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.types.complex._

class AvroTypeSpec extends FlatSpec with ShouldMatchers {

  "The AvroType companion object" should "return valid primitive avro types" in {
    AvroType.fromType[Boolean] should be (Success(AvroBoolean))
    AvroType.fromType[Seq[Byte]] should be (Success(AvroBytes))
    AvroType.fromType[Double] should be (Success(AvroDouble))
    AvroType.fromType[Float] should be (Success(AvroFloat))
    AvroType.fromType[Int] should be (Success(AvroInt))
    AvroType.fromType[Long] should be (Success(AvroLong))
    AvroType.fromType[Unit] should be (Success(AvroNull))
    AvroType.fromType[String] should be (Success(AvroString))
  }

}