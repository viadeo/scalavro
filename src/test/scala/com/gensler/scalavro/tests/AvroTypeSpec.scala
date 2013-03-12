package com.gensler.scalavro.tests

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.types.complex._

/** for testing AvroRecord below */
case class Person(name: String, age: Int)

class AvroTypeSpec extends FlatSpec with ShouldMatchers {

  /**
    * Set this value to `true` to enable printing of JSON schemata to STDOUT.
    */
  val DEBUG = false

  // primitives
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

  // arrays
  it should "return valid AvroArray types" in {
    AvroType.fromType[Seq[Int]] match {
      case Success(avroType) => {
        avroType.isInstanceOf[AvroArray[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Seq[Int]] should be (true)
        if (DEBUG) println(avroType.schema)
      }
      case _ => fail
    }

    AvroType.fromType[List[Boolean]] match {
      case Success(avroType) => {
        avroType.isInstanceOf[AvroArray[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[List[Boolean]] should be (true)
        if (DEBUG) println(avroType.schema)
      }
      case _ => fail
    }

    AvroType.fromType[List[List[Seq[Byte]]]] match {
      case Success(avroType) => {
        avroType.isInstanceOf[AvroArray[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[List[List[Seq[Byte]]]] should be (true)
        if (DEBUG) println(avroType.schema)
      }
      case _ => fail
    }
  }

  // maps
  it should "return valid AvroMap types" in {
    AvroType.fromType[Map[String, Int]] match {
      case Success(avroType) => {
        avroType.isInstanceOf[AvroMap[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Map[String, Int]] should be (true)
        if (DEBUG) println(avroType.schema)
      }
      case _ => fail
    }

    AvroType.fromType[Map[String, List[Double]]] match {
      case Success(avroType) => {
        avroType.isInstanceOf[AvroMap[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Map[String, List[Double]]] should be (true)
        if (DEBUG) println(avroType.schema)
      }
      case _ => fail
    }
  }

  // unions
  it should "return valid AvroUnion types for disjoint unions of two types" in {
    AvroType.fromType[Either[Double, Int]] match {
      case Success(avroType) => {
        avroType.isInstanceOf[AvroUnion[_, _]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Either[Double, Int]] should be (true)
        if (DEBUG) println(avroType.schema)
      }
      case _ => fail
    }

    AvroType.fromType[Either[List[Double], Map[String, Seq[Int]]]] match {
      case Success(avroType) => {
        avroType.isInstanceOf[AvroUnion[_, _]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Either[List[Double], Map[String, Seq[Int]]]] should be (true)
        if (DEBUG) println(avroType.schema)
      }
      case _ => fail
    }
  }

  // records
  it should "return valid AvroRecord types for product types" in {
    AvroType.fromType[Person] match {
      case Success(avroType) => {
        avroType.isInstanceOf[AvroRecord[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Person] should be (true)
        if (DEBUG) println(avroType.schema)
      }
      case _ => fail
    }
  }

}