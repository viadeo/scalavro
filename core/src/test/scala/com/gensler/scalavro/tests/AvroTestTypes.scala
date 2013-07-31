package com.gensler.scalavro.tests

import com.gensler.scalavro.util.FixedData

import scala.collection.immutable

// for testing AvroRecord
case class Person(name: String, age: Int)
case class SantaList(nice: Seq[Person], naughty: Seq[Person])

// cyclic dependency to test dependency robustness
case class A(b: B)
case class B(a: A)

// types for protocol testing
case class Greeting(message: String)
case class Curse(message: String)

sealed class Alpha
abstract class Beta extends Alpha
case class Gamma() extends Alpha
case class Delta() extends Beta
case class Epsilon[T]() extends Beta
case class AlphaWrapper(inner: Alpha)

class AlphaCollection(as: Seq[Alpha]) extends Alpha with Seq[Alpha] {
  def apply(idx: Int): Alpha = as apply idx
  def iterator: Iterator[Alpha] = as.iterator
  def length: Int = as.length
}
object AlphaCollection {
  def apply(as: Alpha*): AlphaCollection = new AlphaCollection(as)
}

// good fixed data example
@FixedData.Length(16)
case class MD5(override val bytes: immutable.Seq[Byte])
  extends FixedData(bytes)

// bad fixed data example
@FixedData.Length(4)
case class BadFixed[T](override val bytes: immutable.Seq[Byte])
  extends FixedData(bytes)