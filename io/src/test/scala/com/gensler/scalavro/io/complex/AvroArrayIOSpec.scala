package com.gensler.scalavro.io.complex

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.error._
import com.gensler.scalavro.io.AvroTypeIO.Implicits._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class AvroArrayIOSpec extends FlatSpec with ShouldMatchers {

  val intArrayType = AvroType.fromType[Seq[Int]].get

  "AvroArrayIO" should "read and write arrays" in {
    val s1 = (0 to 1000).toSeq

    val out = new ByteArrayOutputStream
    intArrayType.write(s1, out)

    val in = new ByteArrayInputStream(out.toByteArray)
    intArrayType read in should equal (Success(s1))
  }

}