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
    AvroType.fromTypeTag[Boolean] should be (Success(AvroBoolean))
    AvroType.fromTypeTag[Seq[Byte]] should be (Success(AvroBytes))
    AvroType.fromTypeTag[Double] should be (Success(AvroDouble))
    AvroType.fromTypeTag[Float] should be (Success(AvroFloat))
    AvroType.fromTypeTag[Int] should be (Success(AvroInt))
    AvroType.fromTypeTag[Long] should be (Success(AvroLong))
    AvroType.fromTypeTag[Unit] should be (Success(AvroNull))
    AvroType.fromTypeTag[String] should be (Success(AvroString))
  }

  it should "return valid avro array types" in {
    AvroType.fromTypeTag[Seq[Int]] match {
      case Success(intSeqType) => {
        intSeqType.isInstanceOf[AvroArray[Int]] should be (true)
        println(intSeqType.schema)
      }
      case _ => fail
    }

    AvroType.fromTypeTag[Map[String, Int]] match {
      case Success(intMapType) => {
        intMapType.isInstanceOf[AvroMap[Int]] should be (true)
        println(intMapType.schema)
      }
      case _ => fail
    }
    
  }

}