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

class AvroJavaByteIOSpec extends FlatSpec with Matchers {

  val io = AvroJavaByteIO

  "AvroJavaByteIO" should "be the AvroTypeIO for AvroJavaByte" in {
    val avroTypeIO: AvroTypeIO[_] = AvroJavaByte.io
    avroTypeIO should be (io)
  }

  it should "read and write java.lang.Bytes" in {
    val out = new ByteArrayOutputStream

    io.write(5.toByte.asInstanceOf[java.lang.Byte], out)
    io.write(2.toByte.asInstanceOf[java.lang.Byte], out)
    io.write(null, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    io read in should equal (Success(5.toByte.asInstanceOf[java.lang.Byte]))
    io read in should equal (Success(2.toByte.asInstanceOf[java.lang.Byte]))
    io read in should equal (Success(null: java.lang.Byte))
  }

  it should "read and write java.lang.Bytes as JSON" in {
    val json = io writeJson 5.toByte.asInstanceOf[java.lang.Byte]
    io readJson json should equal (Success(5.toByte.asInstanceOf[java.lang.Byte]))

    val nullJson = io writeJson null
    io readJson nullJson should equal (Success(null: java.lang.Byte))
  }
}