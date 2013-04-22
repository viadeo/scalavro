package com.gensler.scalavro.types
 
import scala.reflect.runtime.universe._

import spray.json._
import spray.json.DefaultJsonProtocol._

/**
  * Parent class of all composite and parameterized Avro types.
  */
abstract class AvroComplexType[T: TypeTag]
         extends AvroType[T]
         with SelfDescribingSchemaHelpers {

  final val isPrimitive = false

  override def toString(): String = {
    "%s[%s]".format(getClass.getSimpleName, typeOf[T])
  }

  protected def withoutDocOrAliases(json: JsValue): JsValue = json match {
    case JsObject(fields) => new JsObject( fields -- Seq("Doc", "aliases") )
    case otherJsValue: JsValue => otherJsValue
  }

}


trait SelfDescribingSchemaHelpers {

  protected def selfContainedSchemaOrFullyQualifiedName(
    avroType: AvroType[_],
    resolvedSymbols: scala.collection.mutable.Set[String]
  ): JsValue = {

    avroType match {

      case namedType: AvroNamedType[_] => {
        if (resolvedSymbols contains namedType.fullyQualifiedName)
          namedType.fullyQualifiedName.toJson
        else {
          val itemSchema = namedType selfContainedSchema resolvedSymbols
          resolvedSymbols += namedType.fullyQualifiedName
          itemSchema
        }
      }

      case _ => avroType selfContainedSchema resolvedSymbols
    }
  }

}
