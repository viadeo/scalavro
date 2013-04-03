package com.gensler.scalavro.io.complex

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.error._

import com.gensler.scalavro.io.AvroTypeIO

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

// for testing
case class Person(name: String, age: Int)

class AvroRecordIOSpec extends FlatSpec with ShouldMatchers {

  val personType = AvroType.fromType[Person].get.asInstanceOf[AvroRecord[Person]]
  val io = AvroRecordIO(personType)

  "AvroRecordIO" should "instantiate implicitly from an AvroRecord" in {
    import com.gensler.scalavro.io.AvroTypeIO.Implicits._
    val avroTypeIO: AvroTypeIO[_] = personType
    avroTypeIO should equal (io)
  }

  it should "write and read values to a to/from a stream" in {
    val out = new ByteArrayOutputStream

    val julius = Person("Julius Caesar", 2112)

    io.write(julius, out)

    val in = new ByteArrayInputStream(out.toByteArray)

    io read in should equal (Success(julius))
  }

}