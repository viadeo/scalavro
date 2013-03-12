package com.gensler.scalavro

import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.AvroRecord

import spray.json._
import scala.language.existentials

object JsonSchemaProtocol extends DefaultJsonProtocol {

  implicit object JsonSchemifiableWriter extends RootJsonFormat[JsonSchemifiable] {
    def write(objectWithSchema: JsonSchemifiable): JsValue = objectWithSchema.schema
    def read(json: JsValue): JsonSchemifiable = ???
  }

  implicit object AvroTypeWriter extends RootJsonFormat[AvroType[_]] {
    def write(at: AvroType[_]): JsValue = at.asInstanceOf[JsonSchemifiable].toJson
    def read(json: JsValue): AvroType[_] = ???
  }

  implicit object AvroRecordFieldWriter extends RootJsonFormat[AvroRecord.Field[_]] {
    def write(at: AvroRecord.Field[_]): JsValue = at.asInstanceOf[JsonSchemifiable].toJson
    def read(json: JsValue): AvroRecord.Field[_] = ???
  }

 }