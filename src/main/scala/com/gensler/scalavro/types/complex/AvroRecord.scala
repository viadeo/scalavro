package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroNamedType}
import com.gensler.scalavro.JsonSchemifiable
import com.gensler.scalavro.JsonSchemaProtocol._
import scala.reflect.runtime.{universe => ru}
import scala.util.Try
import spray.json._

class AvroRecord[T <: Product : ru.TypeTag](
  val name: String,
  val namespace: String,
  val fields: Seq[AvroRecord.Field[_]],
  val aliases: Seq[String] = Seq(),
  val doc: Option[String] = None
) extends AvroNamedType[T] {

  import AvroRecord._

  val typeName = "record"

  def write(obj: T): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try { ???.asInstanceOf[T] }

  override def schema() = {
    val requiredParams = Map(
      "name"      -> name.toJson,
      "namespace" -> namespace.toJson,
      "fields"    -> fields.toJson
    )

    val aliasesParam = Map("aliases" -> aliases).collect {
      case (k, s) if s.nonEmpty => (k, s.toJson) }

    val docParam = Map("doc" -> doc).collect {
      case (k, Some(v)) => (k, v.toJson) }

    (requiredParams ++ aliasesParam ++ docParam).toJson
  }

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

  def parsingCanonicalForm() = ???
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
  ) extends JsonSchemifiable {

    def schema(): spray.json.JsValue = {
      val requiredParams = Map(
        "name" -> name.toJson,
        "fieldType" -> fieldType.schemaOrName
      )

      val defaultParam = Map("default" -> default).collect {
        case (k, Some(u)) => (k, fieldType.write(u).toJson) }

      val orderParam = Map("order" -> order).collect {
        case (k, Some(o)) => (k, o.schema) }

      val aliasesParam = Map("aliases" -> aliases).collect {
        case (k, s) if s.nonEmpty => (k, s.toJson) }

      val docParam = Map("doc" -> doc).collect {
        case (k, Some(v)) => (k, v.toJson) }

      (requiredParams ++ defaultParam ++ orderParam ++ aliasesParam ++ docParam).toJson
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