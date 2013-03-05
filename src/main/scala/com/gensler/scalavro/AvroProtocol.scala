package com.gensler.scalavro

import com.gensler.scalavro.types.AvroNamedType

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
  messages: Seq[AvroProtocolMessage],
  namespace: Option[String] = None,
  doc: Option[String] = None
)
