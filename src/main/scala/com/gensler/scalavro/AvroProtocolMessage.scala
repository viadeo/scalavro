package com.gensler.scalavro

import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.{AvroRecord, AvroUnion}

import scala.language.existentials

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
case class AvroProtocolMessage(
  request: AvroRecord[_ <: Product],
  response: AvroType[_],
  error: Option[AvroUnion[_, _]] = None,
  doc: Option[String] = None,
  oneWay: Option[Boolean] = None
)