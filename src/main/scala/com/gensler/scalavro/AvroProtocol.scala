package com.gensler.scalavro

import com.gensler.scalavro.types.{AvroType, AvroNamedType}
import com.gensler.scalavro.types.complex.{AvroRecord, AvroUnion}
import com.gensler.scalavro.JsonSchemaProtocol._
import scala.language.existentials
import spray.json._

/**
  * Avro protocols describe RPC interfaces. Like schemas, they are defined with
  * JSON text.
  * 
  * A protocol is a JSON object with the following attributes:
  * 
  * protocol, a string, the name of the protocol (required);
  *
  * namespace, an optional string that qualifies the name;
  *
  * doc, an optional string describing this protocol;
  *
  * types, an optional list of definitions of named types (records, enums,
  * fixed and errors). An error definition is just like a record definition
  * except it uses "error" instead of "record". Note that forward references
  * to named types are not permitted.
  *
  * messages, an optional JSON object whose keys are message names and whose
  * values are objects whose attributes are described below. No two messages
  * may have the same name.
  *
  * The name and namespace qualification rules defined for schema objects apply
  * to protocols as well.
  */
case class AvroProtocol(
  protocol: String,
  types: Seq[AvroNamedType[_]],
  messages: Seq[AvroProtocol.Message],
  namespace: Option[String] = None,
  doc: Option[String] = None
) extends JsonSchemifiable {

  import scala.util.Sorting, scala.math.Ordering

  private[scalavro] implicit object DependencyOrdering extends Ordering[AvroNamedType[_]] {
    def compare(t1: AvroNamedType[_], t2: AvroNamedType[_]) = {
      ((t1 dependsOn t2), (t2 dependsOn t1)) match {
        case (false, false) => 0  // no dependencies, order doesn't matter
        case (true, false)  => -1 // t1 needs to be declared before t2
        case (false, true)  => 1  // t2 needs to be declared before t1
        case (true, true)   => throw new IllegalArgumentException(
          "Detected a symmetric dependency between types [%s] and [%s].".format(t1, t2)
        )
      }
    }
  }

  lazy val normalizedDeclarations: Seq[AvroNamedType[_]] = Sorting.stableSort(types)

  def schema(): JsValue = {
    val requiredParams = Map(
      "protocol" -> protocol.toJson,
      "types" -> normalizedDeclarations.asInstanceOf[JsonSchemifiable].toJson,
      "messages" -> messages.asInstanceOf[JsonSchemifiable].toJson
    )

    val optionalParams = Map(
      "namespace" -> namespace,
      "doc" -> doc
    ) collect { case (k, Some(v)) => (k, v.toJson) }

    (requiredParams ++ optionalParams).toJson
  }

}

object AvroProtocol {
  /**
    * A message has attributes:
    * 
    * a doc, an optional description of the message,
    * 
    * a request, a list of named, typed parameter schemas (this has the same form
    * as the fields of a record declaration);
    * 
    * a response schema;
    * 
    * an optional union of declared error schemas. The effective union has
    * "string" prepended to the declared union, to permit transmission of
    * undeclared "system" errors. For example, if the declared error union is
    * ["AccessError"], then the effective union is ["string", "AccessError"].
    * When no errors are declared, the effective error union is ["string"].
    * Errors are serialized using the effective union; however, a protocol's
    * JSON declaration contains only the declared union.
    * 
    * an optional one-way boolean parameter.
    * 
    * A request parameter list is processed equivalently to an anonymous record.
    * Since record field lists may vary between reader and writer, request
    * parameters may also differ between the caller and responder, and such
    * differences are resolved in the same manner as record field differences.
    * 
    * The one-way parameter may only be true when the response type is "null" and
    * no errors are listed.
    */
  case class Message(
    request: AvroRecord[_ <: Product],
    response: AvroType[_],
    error: Option[AvroUnion[_, _]] = None,
    doc: Option[String] = None,
    oneWay: Option[Boolean] = None
  ) extends JsonSchemifiable {

    def schema(): JsValue = {
      val requiredParams = Map(
        "request" -> request.schema,
        "response" -> response.schema
      )

      val errorParam = Map("error" -> error) collect {
        case (k, Some(union)) => (k, union.schema)
      }

      val docParam = Map("doc" -> doc) collect {
        case (k, Some(v)) => (k, v.toJson)
      }

      val oneWayParam = Map("one-way" -> oneWay) collect {
        case (k, Some(v)) => (k, v.toJson)
      }

      (requiredParams ++ errorParam ++ docParam ++ oneWayParam).toJson
    }

  }
}
