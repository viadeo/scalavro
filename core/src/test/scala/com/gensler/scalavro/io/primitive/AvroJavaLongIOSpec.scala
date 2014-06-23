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

class AvroJavaLongIOSpec extends FlatSpec with Matchers {

  val io = AvroJavaLongIO

  val v1 = (-55L).asInstanceOf[java.lang.Long]
  val v2 = (8675309L).asInstanceOf[java.lang.Long]
  val vMin = Long.MinValue.asInstanceOf[java.lang.Long]
  val vMax = Long.MaxValue.asInstanceOf[java.lang.Long]

  "AvroJavaLongIO" should "be the AvroTypeIO for AvroJavaLong" in {
    val avroTypeIO: AvroTypeIO[_] = AvroJavaLong.io
    avroTypeIO should be (io)
  }

  it should "read and write java.lang.Longs" in {
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
    io read in should equal (Success(null: java.lang.Long))
  }

  it should "read and write java.lang.Longs as JSON" in {
    val json1 = io writeJson v1
    val json2 = io writeJson v2
    val jsonMin = io writeJson vMin
    val jsonMax = io writeJson vMax
    val jsonNull = io writeJson null

    io readJson json1 should equal (Success(v1))
    io readJson json2 should equal (Success(v2))
    io readJson jsonMin should equal (Success(vMin))
    io readJson jsonMax should equal (Success(vMax))
    io readJson jsonNull should equal (Success(null))
  }

}