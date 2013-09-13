package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.AvroTypeIO.Implicits._
import com.gensler.scalavro.io.primitive.AvroLongIO
import com.gensler.scalavro.types.complex.AvroArray
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }
import com.gensler.scalavro.util.ReflectionHelpers

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{ GenericData, GenericArray, GenericDatumWriter }
import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import java.io.{ InputStream, OutputStream }

case class AvroArrayIO[T, S <: Seq[T]](avroType: AvroArray[T, S]) extends AvroTypeIO[S]()(avroType.originalTypeTag) {

  implicit def itemTypeTag = avroType.itemType.tag
  implicit def originalTypeTag = avroType.originalTypeTag

  val originalTypeVarargsApply = ReflectionHelpers.companionVarargsApply[S] match {
    case Some(methodMirror) => methodMirror
    case None => throw new IllegalArgumentException(
      "Sequence subclasses must have a companion object with a public varargs " +
        "apply method, but no such method was found for type [%s].".format(avroType.originalTypeTag.tpe)
    )
  }

  protected lazy val avroSchema: Schema = (new Parser) parse avroType.selfContainedSchema().toString
  val itemIO = AvroTypeIO.Implicits.avroTypeToIO(avroType.itemType)

  protected[scalavro] def asGeneric[G <: Seq[T]: TypeTag](items: G): GenericArray[Any] = {
    import scala.collection.JavaConversions.seqAsJavaList
    val genericArray = new GenericData.Array[Any](items.size, avroSchema)
    genericArray addAll seqAsJavaList(items map { itemIO.asGeneric })
    genericArray
  }

  def write[G <: Seq[T]: TypeTag](items: G, encoder: BinaryEncoder) = {
    try {
      encoder.writeArrayStart
      encoder.setItemCount(items.size)
      for (item <- items) {
        encoder.startItem
        avroType.itemType.write(item, encoder)
      }
      encoder.writeArrayEnd
      encoder.flush
    }
    catch {
      case cause: Throwable =>
        throw new AvroSerializationException(items, cause)
    }
  }

  def read(decoder: BinaryDecoder) = Try {
    val items = new scala.collection.mutable.ArrayBuffer[T]

    def readBlock(): Long = {
      val numItems = (AvroLongIO read decoder).get
      val absNumItems = math abs numItems
      if (numItems < 0L) { val bytesInBlock = (AvroLongIO read decoder).get }
      (0L until absNumItems) foreach { _ => items += avroType.itemType.read(decoder).get }
      absNumItems
    }

    var itemsRead = readBlock()
    while (itemsRead != 0L) { itemsRead = readBlock() }
    originalTypeVarargsApply(items).asInstanceOf[S] // a Seq is passed to varargs MethodMirror.apply
  }

}