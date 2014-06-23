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

class AvroJavaCharacterIOSpec extends FlatSpec with Matchers {

  val io = AvroJavaCharacterIO

  "AvroJavaCharacterIO" should "be the AvroTypeIO for AvroJavaCharacter" in {
    val avroTypeIO: AvroTypeIO[_] = AvroJavaCharacter.io
    avroTypeIO should be (io)
  }

  it should "read and write java.lang.Characters" in {
    val out = new ByteArrayOutputStream

    io.write('A'.asInstanceOf[java.lang.Character], out)
    io.write('%'.asInstanceOf[java.lang.Character], out)
    io.write(null, out)

    val bytes = out.toByteArray
    val in = new ByteArrayInputStream(bytes)

    io read in should equal (Success('A'.asInstanceOf[java.lang.Character]))
    io read in should equal (Success('%'.asInstanceOf[java.lang.Character]))
    io read in should equal (Success(null))
  }

  it should "read and write java.lang.Characters as JSON" in {
    val aJson = io writeJson 'A'.asInstanceOf[java.lang.Character]
    val percentJson = io writeJson '%'.asInstanceOf[java.lang.Character]
    val nullJson = io writeJson null

    io readJson aJson should equal (Success('A'.asInstanceOf[java.lang.Character]))
    io readJson percentJson should equal (Success('%'.asInstanceOf[java.lang.Character]))
    io readJson nullJson should equal (Success(null))
  }

}