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

class AvroJavaShortIOSpec extends FlatSpec with Matchers {

  val io = AvroJavaShortIO

  val v1 = -55.toShort.asInstanceOf[java.lang.Short]
  val v2 = 65535.toShort.asInstanceOf[java.lang.Short]
  val vMin = Short.MinValue.asInstanceOf[java.lang.Short]
  val vMax = Short.MaxValue.asInstanceOf[java.lang.Short]

  "AvroJavaShortIO" should "be the AvroTypeIO for AvroJavaShort" in {
    val avroTypeIO: AvroTypeIO[_] = AvroJavaShort.io
    avroTypeIO should be (io)
  }

  it should "read and write Shorts" in {
    val out = new ByteArrayOutputStream

    io.write(v1, out)
    io.write(v2, out)
    io.write(vMin, out)
    io.write(vMax, out)
    io.write(null, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    io read in should equal (Success(v1))
    io read in should equal (Success(v2))
    io read in should equal (Success(vMin))
    io read in should equal (Success(vMax))
    io read in should equal (Success(null: java.lang.Short))
  }

  it should "read and write Shorts as JSON" in {
    val json1 = io writeJson v1
    val json2 = io writeJson v2
    val jsonMin = io writeJson vMin
    val jsonMax = io writeJson vMax
    val jsonNull = io writeJson null

    io readJson json1 should equal (Success(v1))
    io readJson json2 should equal (Success(v2))
    io readJson jsonMin should equal (Success(vMin))
    io readJson jsonMax should equal (Success(vMax))
    io readJson jsonNull should equal (Success(null: java.lang.Short))
  }

}