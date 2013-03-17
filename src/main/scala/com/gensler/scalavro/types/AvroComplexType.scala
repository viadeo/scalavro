package com.gensler.scalavro.types
 
import scala.reflect.runtime.universe._

import spray.json._

abstract class AvroComplexType[T: TypeTag] extends AvroType[T] {
  final val isPrimitive = false

  override def toString(): String = {
    "%s[%s]".format(getClass.getSimpleName, typeOf[T])
  }

  protected def withoutDocOrAliases(json: JsValue): JsValue = json match {
    case JsObject(fields) => new JsObject( fields -- Seq("Doc", "aliases") )
    case otherJsValue: JsValue => otherJsValue
  }

}
