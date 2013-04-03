package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroNamedType}
import com.gensler.scalavro.{JsonSchemifiable, CanonicalForm}
import com.gensler.scalavro.JsonSchemaProtocol._
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}
import com.gensler.scalavro.util.ReflectionHelpers._

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericRecord, GenericData, GenericDatumWriter, GenericDatumReader}
import org.apache.avro.io.{EncoderFactory, DecoderFactory}

import spray.json._

import scala.reflect.runtime.universe._
import scala.reflect.ClassTag
import scala.util.{Try, Success, Failure}
import scala.collection.immutable.ListMap

import java.io.{InputStream, OutputStream}

class AvroRecord[T : TypeTag](
  val name: String,
  val fields: Seq[AvroRecord.Field[_]],
  val aliases: Seq[String] = Seq(),
  val namespace: Option[String] = None,
  val doc: Option[String] = None
) extends AvroNamedType[T] {

  import AvroRecord._

  val typeName = "record"

  // result of Apache implementation's Schema.Parser.parse
  protected lazy val avroSchema: Schema = (new Parser) parse schema.toString

  /**
    * Returns the Apache implementation's GenericRecord representation of this
    * AvroRecord.
    */
  protected[scalavro] def asGenericRecord(obj: T): GenericRecord = {
    val record = new GenericData.Record(avroSchema)

    fields.foreach { field =>
      productElement(obj, field.name)(typeTag[T], field.fieldType.tag) map { value =>
        record.put(field.name, value) // primitives only for now...
      }
    }

    record
  }

  /**
    * Writes a binary representation of the supplied object to the supplied
    * stream.
    */
  def write(obj: T, stream: OutputStream) {
    try {
      val encoder = EncoderFactory.get.binaryEncoder(stream, null)
      val datumWriter = new GenericDatumWriter[GenericRecord](avroSchema)
      datumWriter.write(asGenericRecord(obj), encoder)
      encoder.flush
    }
    catch { case t: Throwable => throw new AvroSerializationException[T](obj) }
  }

  /**
    * Reads a binary representation of the underlying Scala type from the
    * supplied stream.
    */
  def read(stream: InputStream) = Try {
    val datumReader = new GenericDatumReader[GenericRecord](avroSchema)
    val decoder = DecoderFactory.get.binaryDecoder(stream, null)
    val record = datumReader.read(null.asInstanceOf[GenericRecord], decoder)

    val args = fields map { field => record.get(field.name) match {
      case utf8: org.apache.avro.util.Utf8 => utf8.toString
      case other: Any => other
    }}

    instantiateCaseClassWith[T](args).get
  }

  def writeAsJson(obj: T): JsValue = ??? // TODO

  def readFromJson(json: JsValue) = Try { ???.asInstanceOf[T] } // TODO

  // name, type, fields, symbols, items, values, size
  override def schema() = {
    val requiredParams = ListMap(
      "name"      -> name.toJson,
      "type"      -> typeName.toJson,
      "fields"    -> fields.toJson,
      "namespace" -> namespace.toJson
    )

    val aliasesParam = ListMap("aliases" -> aliases).collect {
      case (k, s) if s.nonEmpty => (k, s.toJson) }

    val docParam = ListMap("doc" -> doc).collect {
      case (k, Some(v)) => (k, v.toJson) }

    new JsObject(requiredParams ++ aliasesParam ++ docParam)
  }

  override def parsingCanonicalForm(): JsValue = new JsObject(ListMap(
    "name"   -> fullyQualifiedName.toJson,
    "type"   -> typeName.toJson,
    "fields" -> fields.asInstanceOf[Seq[CanonicalForm]].toJson
  ))

  def dependsOn(thatType: AvroType[_]) = {
    fields.foldLeft(false) { (dependencyFound, field) =>
      dependencyFound ||
      field.fieldType == thatType ||
      (field.fieldType dependsOn thatType)
    }
  }

  override def toString(): String = {
    "%s[%s]".format(getClass.getSimpleName, name)
  }

}

object AvroRecord {

  /**
    * Records fields have:
    * 
    * name: a JSON string providing the name of the field (required)
    * 
    * doc: a JSON string describing this field for users (optional).
    * 
    * type: A JSON object defining a schema, or a JSON string naming a record
    * definition (required).
    * 
    * default: A default value for this field, used when reading instances that
    * lack this field (optional). Permitted values depend on the field's schema
    * type. Default values for union fields correspond to the first schema in
    * the union. Default values for bytes and fixed fields are JSON strings,
    * where Unicode code points 0-255 are mapped to unsigned 8-bit byte values
    * 0-255.
    *
    * order
    *
    * aliases
    */
  case class Field[U](
    name: String,
    fieldType: AvroType[U],
    default: Option[U] = None,
    order: Option[Order] = None,
    aliases: Seq[String] = Seq(),
    doc: Option[String] = None
  ) extends JsonSchemifiable with CanonicalForm {

    // name, type, fields, symbols, items, values, size
    def schema(): spray.json.JsValue = {
      val requiredParams = ListMap(
        "name" -> name.toJson,
        "type" -> fieldType.schemaOrName
      )

      val defaultParam = ListMap("default" -> default).collect {
        case (k, Some(u)) => (k, fieldType writeAsJson u) }

      val orderParam = ListMap("order" -> order).collect {
        case (k, Some(o)) => (k, o.schema) }

      val aliasesParam = ListMap("aliases" -> aliases).collect {
        case (k, s) if s.nonEmpty => (k, s.toJson) }

      val docParam = ListMap("doc" -> doc).collect {
        case (k, Some(v)) => (k, v.toJson) }

      new JsObject(requiredParams ++ defaultParam ++ orderParam ++ aliasesParam ++ docParam)
    }

    def parsingCanonicalForm(): JsValue = {
      val requiredParams = ListMap(
        "name" -> name.toJson,
        "type" -> fieldType.canonicalFormOrFullyQualifiedName
      )

      val defaultParam = ListMap("default" -> default).collect {
        case (k, Some(u)) => (k, fieldType writeAsJson u) }

      val orderParam = ListMap("order" -> order).collect {
        case (k, Some(o)) => (k, o.schema) }

      new JsObject(requiredParams ++ defaultParam ++ orderParam)
    }

  }

  trait Order {
    def value(): String
    final def schema(): JsValue = value.toJson
  }

  object Order {
    case object Ascending extends Order { val value = "ascending" }
    case object Descending extends Order { val value = "descending" }
    case object Ignore extends Order { val value = "ignore" }
  }

}