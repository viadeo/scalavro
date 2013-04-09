package com.gensler.scalavro.tests

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.AvroProtocol

class AvroTypeSpec extends AvroSpec {

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
        // prettyPrint(avroType.schema)

        avroType.isInstanceOf[AvroArray[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Seq[Int]] should be (true)
      }
      case _ => fail
    }

    AvroType.fromType[Seq[Seq[Seq[Byte]]]] match {
      case Success(avroType) => {
        // prettyPrint(avroType.schema)

        avroType.isInstanceOf[AvroArray[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Seq[Seq[Seq[Byte]]]] should be (true)
      }
      case _ => fail
    }
  }

  // maps
  it should "return valid AvroMap types" in {
    AvroType.fromType[Map[String, Int]] match {
      case Success(avroType) => {
        // prettyPrint(avroType.schema)

        avroType.isInstanceOf[AvroMap[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Map[String, Int]] should be (true)
      }
      case _ => fail
    }

    AvroType.fromType[Map[String, Seq[Double]]] match {
      case Success(avroType) => {
        // prettyPrint(avroType.schema)

        avroType.isInstanceOf[AvroMap[_]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Map[String, Seq[Double]]] should be (true)
      }
      case _ => fail
    }
  }

  // unions
  it should "return valid AvroUnion types for disjoint unions of two types" in {
    AvroType.fromType[Either[Double, Int]] match {
      case Success(avroType) => {
        // prettyPrint(avroType.schema)

        avroType.isInstanceOf[AvroUnion[_, _]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Either[Double, Int]] should be (true)
      }
      case _ => fail
    }

    AvroType.fromType[Either[Seq[Double], Map[String, Seq[Int]]]] match {
      case Success(avroType) => {
        // prettyPrint(avroType.schema)

        avroType.isInstanceOf[AvroUnion[_, _]] should be (true)
        typeOf[avroType.scalaType] =:= typeOf[Either[Seq[Double], Map[String, Seq[Int]]]] should be (true)
      }
      case _ => fail
    }
  }

  // records
  it should "return valid AvroRecord types for product types" in {

    val personType = AvroType.fromType[Person].get
    val santaListType = AvroType.fromType[SantaList].get

    // prettyPrint(personType.schema)
    // prettyPrint(personType.parsingCanonicalForm)

    personType.isInstanceOf[AvroRecord[_]] should be (true)
    typeOf[personType.scalaType] =:= typeOf[Person] should be (true)
    val personRecord = personType.asInstanceOf[AvroRecord[Person]]
    personRecord.namespace should be (Some("com.gensler.scalavro.tests"))
    personRecord.name should be ("Person")
    personRecord.fullyQualifiedName should be ("com.gensler.scalavro.tests.Person")
    personType dependsOn personType should be (false)
    personType dependsOn santaListType should be (false)
 
    // prettyPrint(santaListType.schema)
    // prettyPrint(santaListType.parsingCanonicalForm)

    santaListType.isInstanceOf[AvroRecord[_]] should be (true)
    typeOf[santaListType.scalaType] =:= typeOf[SantaList] should be (true)
    val santaListRecord = santaListType.asInstanceOf[AvroRecord[SantaList]]
    santaListRecord.namespace should be (Some("com.gensler.scalavro.tests"))
    santaListRecord.name should be ("SantaList")
    santaListRecord.fullyQualifiedName should be ("com.gensler.scalavro.tests.SantaList")
    santaListType dependsOn personType should be (true)
    santaListType dependsOn santaListType should be (false)
  }

  it should "detect dependencies among AvroRecord types" in {
    import com.gensler.scalavro.error.CyclicTypeDependencyException
    evaluating { AvroType.fromType[A].get } should produce [CyclicTypeDependencyException]
    evaluating { AvroType.fromType[B].get } should produce [CyclicTypeDependencyException]
  }

  it should "construct protocol definitions" in {

    val greetingType = AvroType.fromType[Greeting].get.asInstanceOf[AvroRecord[Greeting]]
    val curseType = AvroType.fromType[Curse].get.asInstanceOf[AvroRecord[Curse]]

    val hwProtocol = AvroProtocol(

      protocol = "HelloWorld",

      types    = Seq(greetingType, curseType),

      messages = Map(
                  "hello" -> AvroProtocol.Message(
                    request = Map(
                      "greeting" -> greetingType
                    ),
                    response = greetingType,
                    errors = Some(AvroType.fromType[Either[Curse, String]].get.asInstanceOf[AvroUnion[_,_]]),
                    doc = Some("Say hello.")
                  )
                 ),

      namespace = Some("com.gensler.scalavro.tests"),

      doc       = Some("Protocol Greetings")

    )

    // prettyPrint(hwProtocol.schema)
    // prettyPrint(hwProtocol.parsingCanonicalForm)
  }

}