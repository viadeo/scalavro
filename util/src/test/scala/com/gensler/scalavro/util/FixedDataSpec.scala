package com.gensler.scalavro.util

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import scala.collection.immutable
import scala.reflect.runtime.universe._

case class MissingLength(override val bytes: immutable.Seq[Byte]) extends FixedData(bytes)

@FixedData.Length(4)
case class SmallFixed(override val bytes: immutable.Seq[Byte]) extends FixedData(bytes)

class FixedDataSpec extends FlatSpec with ShouldMatchers {

  val whoaBytes = "whoa".getBytes.toIndexedSeq

  "FixedData" should "initialize" in {
    val smallFixed = SmallFixed(whoaBytes)

    evaluating {
      MissingLength(whoaBytes)
    } should produce[java.lang.AssertionError]
  }

  it should "synthesize a FixedData.Length instance given a ClassSymbol" in {
    val smallFixedSymbol = typeOf[SmallFixed].typeSymbol.asClass
    FixedData.lengthAnnotationInstance(smallFixedSymbol) should be (Some(FixedData.Length(4)))
  }

}