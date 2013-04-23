package com.gensler.scalavro.io.complex

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.error._

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.AvroTypeIO.Implicits._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

// for testing
case class Person(name: String, age: Int)

// for testing
case class SantaList(nice: Seq[Person], naughty: Seq[Person])

class AvroRecordIOSpec extends FlatSpec with ShouldMatchers {

  val personType = AvroType.fromType[Person].get
  val io = personType.io

  "AvroRecordIO" should "instantiate implicitly from an AvroRecord" in {
    val avroTypeIO: AvroTypeIO[_] = personType
    avroTypeIO should equal (io)
  }

  it should "read and write simple records" in {
    val out = new ByteArrayOutputStream
    val julius = Person("Julius Caesar", 2112)
    io.write(julius, out)
    val in = new ByteArrayInputStream(out.toByteArray)
    io read in should equal (Success(julius))
  }

  it should "read and write complex records" in {
    val sList = SantaList(
      nice    = Seq(Person("Suzie", 9)),
      naughty = Seq(Person("Tommy", 7))
    )

    val santaListType = AvroType.fromType[SantaList].get
    val santaIO = santaListType.io

    val out = new ByteArrayOutputStream
    santaIO.write(sList, out)

    val in = new ByteArrayInputStream(out.toByteArray)
    val Success(readResult) = santaIO read in

    readResult should equal (sList)
  }

}