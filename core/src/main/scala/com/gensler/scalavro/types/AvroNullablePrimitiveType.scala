package com.gensler.scalavro.types

import scala.reflect.runtime.universe._
import spray.json._
import spray.json.DefaultJsonProtocol._

/**
  * Parent class of all nullable simple Avro types.
  */
abstract class AvroNullablePrimitiveType[T: TypeTag] extends AvroPrimitiveType[T] {

  override def schema(): spray.json.JsValue = Seq("null", this.typeName).toJson

}
