package com.gensler.scalavro.util

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import scala.reflect.runtime.universe._

import com.gensler.scalavro.util.ReflectionHelpers._

class ReflectionHelpersSpec extends FlatSpec with ShouldMatchers {

  object Direction extends Enumeration {
    type Direction = Value
    val NORTH, EAST, SOUTH, WEST = Value
  }

  "The reflection helpers" should "return the enumeration tag for a given enum value" in {
    val et = enumForValue[Direction.type#Value]
    et.tpe =:= typeOf[Direction.type] should be (true)
  }

}