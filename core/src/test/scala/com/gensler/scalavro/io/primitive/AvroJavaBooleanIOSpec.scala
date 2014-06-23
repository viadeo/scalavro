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

class AvroJavaBooleanIOSpec extends FlatSpec with Matchers {

  val io = AvroJavaBooleanIO
  val javaTrue = true.asInstanceOf[java.lang.Boolean]
  val javaFalse = false.asInstanceOf[java.lang.Boolean]

  "AvroJavaBooleanIO" should "be the AvroTypeIO for AvroJavaBoolean" in {
    val avroTypeIO: AvroTypeIO[_] = AvroJavaBoolean.io
    avroTypeIO should be (io)
  }

  it should "write java.lang.Booleans to a stream" in {
    val out = new ByteArrayOutputStream

    io.write(javaTrue, out)
    io.write(javaFalse, out)
    io.write(null, out)

    val bytes = out.toByteArray
    bytes.toSeq should equal (Seq(2.toByte, 1.toByte, 2.toByte, 0.toByte, 0.toByte))

    val in = new ByteArrayInputStream(bytes)

    io read in should equal (Success(javaTrue))
    io read in should equal (Success(javaFalse))
    io read in should equal (Success(null: java.lang.Boolean))
  }

  it should "read and write java.lang.Booleans as JSON" in {
    val trueJson = io writeJson javaTrue
    val falseJson = io writeJson javaFalse
    val nullJson = io writeJson null
    io readJson trueJson should equal (Success(javaTrue))
    io readJson falseJson should equal (Success(javaFalse))
    io readJson nullJson should equal (Success(null: java.lang.Boolean))
  }

}