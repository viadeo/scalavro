package com.gensler.scalavro.io.complex

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.error._
import com.gensler.scalavro.io.AvroTypeIO.Implicits._

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

class AvroMapIOSpec extends FlatSpec with ShouldMatchers {

  val intMapType = AvroType.fromType[Map[String, Int]].get
  val io = intMapType.io

  "AvroMapIO" should "be available with the AvroTypeIO implicits in scope" in {
    io.isInstanceOf[AvroMapIO[_]] should be (true)
  }

  it should "read and write maps" in {

    val m1 = Map(
      "uno" -> 1,
      "due" -> 2,
      "tre" -> 3,
      "quattro" -> 4,
      "cinque" -> 5)

    val out = new ByteArrayOutputStream
    io.write(m1, out)

    val in = new ByteArrayInputStream(out.toByteArray)
    val Success(readResult) = io read in
    readResult should equal (m1)
    readResult.get("cinque") should be (Some(5))
  }

}