package com.gensler.scalavro.tests

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.types.complex._

import org.slf4j.Logger
import org.slf4j.LoggerFactory

// for testing AvroRecord below
case class Person(name: String, age: Int)
case class SantaList(nice: List[Person], naughty: List[Person])

// cyclic dependency to test dependency robustness
case class A(b: B)
case class B(a: A)

class AvroTypeSpec extends FlatSpec with ShouldMatchers {

  val log = LoggerFactory.getLogger(getClass.getName)

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

        log debug avroType.schema.toString

        avroType.isInstanceOf[AvroArray[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Seq[Int]] should be (true)
      }
      case _ => fail
    }

    AvroType.fromType[List[Boolean]] match {
      case Success(avroType) => {

        log debug avroType.schema.toString

        avroType.isInstanceOf[AvroArray[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[List[Boolean]] should be (true)
      }
      case _ => fail
    }

    AvroType.fromType[List[List[Seq[Byte]]]] match {
      case Success(avroType) => {

        log debug avroType.schema.toString

        avroType.isInstanceOf[AvroArray[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[List[List[Seq[Byte]]]] should be (true)
      }
      case _ => fail
    }
  }

  // maps
  it should "return valid AvroMap types" in {
    AvroType.fromType[Map[String, Int]] match {
      case Success(avroType) => {

        log debug avroType.schema.toString

        avroType.isInstanceOf[AvroMap[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Map[String, Int]] should be (true)
      }
      case _ => fail
    }

    AvroType.fromType[Map[String, List[Double]]] match {
      case Success(avroType) => {

        log debug avroType.schema.toString

        avroType.isInstanceOf[AvroMap[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Map[String, List[Double]]] should be (true)
      }
      case _ => fail
    }
  }

  // unions
  it should "return valid AvroUnion types for disjoint unions of two types" in {
    AvroType.fromType[Either[Double, Int]] match {
      case Success(avroType) => {

        log debug avroType.schema.toString

        avroType.isInstanceOf[AvroUnion[_, _]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Either[Double, Int]] should be (true)
      }
      case _ => fail
    }

    AvroType.fromType[Either[List[Double], Map[String, Seq[Int]]]] match {
      case Success(avroType) => {

        log debug avroType.schema.toString

        avroType.isInstanceOf[AvroUnion[_, _]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Either[List[Double], Map[String, Seq[Int]]]] should be (true)
      }
      case _ => fail
    }
  }

  // records
  it should "return valid AvroRecord types for product types" in {
    val personType = AvroType.fromType[Person].get

    log.debug("\n{}\n{}",
      personType.schema,
      personType.parsingCanonicalForm
    )

    personType.isInstanceOf[AvroRecord[_]] should be (true)
    typeOf[personType.scalaType] =:= typeOf[Person] should be (true)
    val personRecord = personType.asInstanceOf[AvroRecord[Person]]
    personRecord.namespace should be (Some("com.gensler.scalavro.tests"))
    personRecord.name should be ("Person")
 
    AvroType.fromType[SantaList] match {
      case Success(santaListType) => {

        log.debug("\n{}\n{}",
          santaListType.schema,
          santaListType.parsingCanonicalForm
        )

        santaListType.isInstanceOf[AvroRecord[_]] should be (true)
        typeOf[santaListType.scalaType] =:= typeOf[SantaList] should be (true)
        val santaListRecord = santaListType.asInstanceOf[AvroRecord[SantaList]]
        santaListRecord.namespace should be (Some("com.gensler.scalavro.tests"))
        santaListRecord.name should be ("SantaList")
        santaListType dependsOn personType should be (true)
      }
      case _ => fail
    }

  }

  it should "detect dependencies among AvroRecord types" in {
    import com.gensler.scalavro.error.CyclicTypeDependencyException
    evaluating { AvroType.fromType[A].get } should produce [CyclicTypeDependencyException]
    evaluating { AvroType.fromType[B].get } should produce [CyclicTypeDependencyException]
  }

}