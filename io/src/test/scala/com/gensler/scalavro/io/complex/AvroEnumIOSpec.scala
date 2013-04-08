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

object Direction extends Enumeration {
  type Direction = Value
  val NORTH, EAST, SOUTH, WEST = Value
}

class AvroEnumIOSpec extends FlatSpec with ShouldMatchers {

  val enumType = AvroType.fromType[Direction.type#Direction].get
  val io = AvroEnumIO(enumType.asInstanceOf[AvroEnum[Direction.type]])

  "AvroEnumIO" should "read and write enumerations" in {
    val out = new ByteArrayOutputStream
    io.write(Direction.NORTH, out)
    io.write(Direction.SOUTH, out)
    io.write(Direction.WEST, out)
    io.write(Direction.EAST, out)

    println(out.toByteArray.toSeq)

    val in = new ByteArrayInputStream(out.toByteArray)

    io read in should equal (Success(Direction.NORTH))
    io read in should equal (Success(Direction.SOUTH))
    io read in should equal (Success(Direction.WEST))
    io read in should equal (Success(Direction.EAST))
  }

}