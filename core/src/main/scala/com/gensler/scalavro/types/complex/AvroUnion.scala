package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroComplexType }
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.JsonSchemaProtocol._
import com.gensler.scalavro.util.ReflectionHelpers

import com.gensler.scalavro.util.Union
import com.gensler.scalavro.util.Union.union

import scala.reflect.runtime.universe._
import scala.collection.mutable
import scala.util.Success

import spray.json._

class AvroUnion[U <: Union.not[_]: TypeTag, T](
    val union: Union[U],
    val originalType: TypeTag[T]) extends AvroComplexType()(originalType) {

  val memberAvroTypes = union.typeMembers.map {
    tpe => AvroType.fromType(ReflectionHelpers tagForType tpe).get
  }

  val typeName = "union"

  def selfContainedSchema(
    resolvedSymbols: mutable.Set[String] = mutable.Set[String]()) = {
    memberAvroTypes.map { at =>
      selfContainedSchemaOrFullyQualifiedName(at, resolvedSymbols)
    }.toJson
  }

}

class AvroReferenceUnion[U <: Union.not[_]: TypeTag, T](
    union: Union[U],
    originalType: TypeTag[T]) extends AvroUnion[U, T](union, originalType) {

  override def selfContainedSchema(resolvedSymbols: mutable.Set[String] = mutable.Set[String]()) = memberAvroTypes.map {
    _ match {
      case recordType: AvroRecord[_] => recordType.selfContainedSchema(resolvedSymbols)
      case at: AvroType[_]           => selfContainedSchemaOrFullyQualifiedName(at, resolvedSymbols)
    }
  }.toJson

}

object AvroUnion {

  import com.gensler.scalavro.Reference

  def referenceUnionFor[T](recordType: AvroRecord[T]): AvroReferenceUnion[_, _] = {
    implicit val recordTypeTag: TypeTag[T] = recordType.tag

    new AvroReferenceUnion(
      new Union[union[T]#or[Reference]],
      typeTag[Either[T, Reference]]
    )
  }

}
