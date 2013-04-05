package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroArray
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericArray

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

case class AvroArrayIO[T](avroType: AvroArray[T]) extends AvroTypeIO[Seq[T]] {

  implicit def itemTypeTag = avroType.itemType.tag
  lazy val avroSchema: Schema = (new Parser) parse avroType.selfContainedSchema().toString
  val itemIO = AvroTypeIO.Implicits.avroTypeToIO(avroType.itemType)

  def asGeneric(items: Seq[T]): GenericArray[Any] = {
    val genericArray = new GenericData.Array[Any](items.size, avroSchema)

    items foreach { item =>
      genericArray add (itemIO asGeneric item)
    }

    genericArray
  }

  def fromGeneric(obj: Any): Seq[T] = {
    import scala.collection.JavaConversions.asScalaBuffer
    import com.gensler.scalavro.io.AvroTypeIO.Implicits._

    obj match {
      case genericArray: GenericArray[_] => {
        val genericSeq = asScalaBuffer(genericArray)
        genericSeq.map { avroType.itemType.fromGeneric }
      }
      case _ => throw new AvroDeserializationException()(avroType.tag)
    }
  }

  def write(obj: Seq[T], stream: OutputStream) = ???

  def read(stream: InputStream) = Try { ???.asInstanceOf[Seq[T]] }

}