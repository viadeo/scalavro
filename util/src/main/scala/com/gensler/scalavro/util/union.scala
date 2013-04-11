package com.gensler.scalavro
package util

import scala.reflect.runtime.universe._

/**
  * Provides a facility for specifying unboxed union types of arbitrary
  * ordinality in the Scala type system.
  *
  * This clever technique was proposed by Miles Sabin:
  * http://chuusai.com/2011/06/09/scala-union-types-curry-howard
  *
  * Usage:
  *
  * import com.gensler.scalavro.util.Union._
  *
  * type UnionISB = union [Int] #or [String] #or [Boolean] #apply
  *
  * def unionPrint[T : prove [UnionISB] #containsType](t: T) =
  *   t match {
  *     case i: Int     => println(i)
  *     case s: String  => println(s)
  *     case b: Boolean => println(b)
  *   }
  *
  * unionPrint(true) // "true"
  * unionPrint(55)   // 55
  */
object Union extends UnionTypeHelpers {
  type union[A] = {
    type or[B] = Disjunction[not[A]]#or[B]
    type apply = not[not[A]]
  }
  type prove[U] = { type containsType[X] = not[not[X]] <:< U }
}

private[scalavro] trait UnionTypeHelpers {

  sealed trait not[-A]

  trait Disjunction[A] {
    type or[B] = Disjunction[A with not[B]]
    type apply = not[A]
  }
}

class Union[U <: Union.not[_] : TypeTag] {

  import Union._

  type underlying = U

  type containsType[X] = prove [U] #containsType [X]

  /**
    * Returns the set of member types of the underlying union.
    */
  def typeMembers(): Set[Type] = {
    val ut = typeOf[U]
    val tParams = ut.typeSymbol.asType.typeParams // List[Symbol]
    val actualParam = tParams.head.asType.toTypeIn(ut)

    val notType = typeOf[Union.not[_]]
    var members = Vector[Type]()

    actualParam.foreach { part => if (part <:< notType) {
      val partParams = part.typeSymbol.asType.typeParams.map { _.asType.toTypeIn(part).normalize }
      members ++= partParams
    }}

    members.toSet
  }

  case class Value[T](ref: T, tag: TypeTag[T])

  private var wrappedValue: Value[_] = Value((), typeTag[Unit])

  /**
    * Returns `true` if the supplied type is a member of this union.
    */
  def contains[X: TypeTag]: Boolean = typeOf[not[not[X]]] <:< typeOf[U]

  def assign[X : TypeTag : containsType](newValue: X) {
    wrappedValue = Value(newValue, typeTag[X])
  }

  def rawValue() = wrappedValue.ref

  def value[X : TypeTag : containsType](): Option[X] = wrappedValue match {
    case Value(x, tag) if tag.tpe <:< typeOf[X] => Some(x.asInstanceOf[X])
    case _ => None
  }

  /**
    * == Java API ==
    */
  @throws[ClassCastException]
  def value[P](prototype: P) = wrappedValue.ref.asInstanceOf[P]

}
