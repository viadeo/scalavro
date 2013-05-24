package com.gensler.scalavro.io.complex

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.complex._
import com.gensler.scalavro.error._

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

class AvroFixedIOSpec extends FlatSpec with ShouldMatchers {

  val md5Type = AvroType[MD5]
  val io = md5Type.io

  "AvroFixedIO" should "be available with the AvroTypeIO implicits in scope" in {

  }

  it should "do other stuff" in {
  }

}