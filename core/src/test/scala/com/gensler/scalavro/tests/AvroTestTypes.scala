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

class Alpha
abstract class Beta extends Alpha
case class Gamma() extends Alpha
case class Delta() extends Beta
case class Epsilon[T]() extends Beta

case class AlphaWrapper(inner: Alpha)

// good fixed data example
@FixedData.Length(16)
case class MD5(override val bytes: immutable.Seq[Byte])
  extends FixedData(bytes)

// bad fixed data example
@FixedData.Length(4)
case class BadFixed[T](override val bytes: immutable.Seq[Byte])
  extends FixedData(bytes)