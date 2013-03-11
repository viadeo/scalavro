package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{AvroType, AvroNamedType}
import com.gensler.scalavro.AvroOrder
import scala.reflect.runtime.{universe => ru}
import scala.util.Try

class AvroRecord[T <: Product : ru.TypeTag](
  name: String,
  namespace: String,
  fields: Seq[AvroRecord.Field[_]],
  aliases: Seq[String] = Seq(),
  doc: Option[String] = None
) extends AvroNamedType[T] {

  val typeName = "record"

  def write(obj: T): Seq[Byte] = ???

  def read(bytes: Seq[Byte]) = Try {
    ???.asInstanceOf[T]
  }

  override def schema() = ???

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
    order: Option[AvroOrder] = None,
    aliases: Seq[String] = Seq(),
    doc: Option[String] = None
  ) {
    def schema(): spray.json.JsValue = ???
  }

}