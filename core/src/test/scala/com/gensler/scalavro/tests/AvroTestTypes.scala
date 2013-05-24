package com.gensler.scalavro.tests

import com.gensler.scalavro.util.{ FixedData, FixedDataCompanion }

// for testing AvroRecord
case class Person(name: String, age: Int)
case class SantaList(nice: Seq[Person], naughty: Seq[Person])

// cyclic dependency to test dependency robustness
case class A(b: B)
case class B(a: A)

// types for protocol testing
case class Greeting(message: String)
case class Curse(message: String)

class Alpha
abstract class Beta extends Alpha
case class Gamma() extends Alpha
case class Delta() extends Beta
case class Epsilon[T]() extends Beta

case class AlphaWrapper(inner: Alpha)

// fixed data example
case class MD5(bytes: Seq[Byte]) extends FixedData

// companion for fixed data
object MD5 extends FixedDataCompanion {
  val length = 16
}
