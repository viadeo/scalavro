package com.gensler.scalavro.util

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import scala.reflect.runtime.universe._

import com.gensler.scalavro.util.ReflectionHelpers._

case class Animal(sound: String)

// case class with multiple constructors
case class Person(name: String, age: Int) {
  def this(name: String) = this(name, 0)
}

class ReflectionHelpersSpec extends FlatSpec with ShouldMatchers {

  object Direction extends Enumeration {
    type Direction = Value
    val NORTH, EAST, SOUTH, WEST = Value
  }

  class A
  abstract class B extends A
  case class C() extends A
  case class D() extends B

  "The reflection helpers object" should "return the enumeration tag for a given enum value" in {
    val et = enumForValue[Direction.type#Value]
    et.tpe =:= typeOf[Direction.type] should be (true)
  }

  it should "return constructor parameters for case classes" in {
    import scala.collection.immutable.ListMap
    caseClassParamsOf[Animal] should be (ListMap("sound" -> typeTag[String]))
  }

  it should "return constructor parameters for case classes with multiple constructors" in {
    import scala.collection.immutable.ListMap
    caseClassParamsOf[Person] should have size (2)
  }

  it should "return the avro-typable subtypes of a given type" in {
    typeableSubTypesOf[A] should have size (2)
    typeableSubTypesOf[B] should have size (1)
  }

}