package com.gensler.scalavro.io.primitive.test

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._
import com.gensler.scalavro.io.primitive._
import com.gensler.scalavro.error._

import com.gensler.scalavro.io.AvroTypeIO

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

class AvroJavaDoubleIOSpec extends FlatSpec with Matchers {

  val io = AvroJavaDoubleIO

  val v1 = math.Pi.asInstanceOf[java.lang.Double]
  val v2 = (1.23).asInstanceOf[java.lang.Double]
  val v3 = (-1500.123).asInstanceOf[java.lang.Double]
  val vMax = Double.MaxValue.asInstanceOf[java.lang.Double]
  val vMin = Double.MinValue.asInstanceOf[java.lang.Double]

  "AvroJavaDoubleIO" should "be the AvroTypeIO for AvroJavaDouble" in {
    val avroTypeIO: AvroTypeIO[_] = AvroJavaDouble.io
    avroTypeIO should be (io)
  }

  it should "read and write java.lang.Doubles" in {
    val out = new ByteArrayOutputStream

    io.write(v1, out)
    io.write(v2, out)
    io.write(v3, out)
    io.write(null, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    io read in should equal (Success(v1))
    io read in should equal (Success(v2))
    io read in should equal (Success(v3))
    io read in should equal (Success(null))
  }

  it should "read and write java.lang.Doubles as JSON" in {
    val piJson = io writeJson v1
    val smallJson = io writeJson v2
    val bigNegJson = io writeJson v3
    val biggestJson = io writeJson vMax
    val smallestJson = io writeJson vMin
    val nullJson = io writeJson null

    io readJson piJson should equal (Success(v1))
    io readJson smallJson should equal (Success(v2))
    io readJson bigNegJson should equal (Success(v3))
    io readJson biggestJson should equal (Success(vMax))
    io readJson smallestJson should equal (Success(vMin))
    io readJson nullJson should equal (Success(null))
  }

}