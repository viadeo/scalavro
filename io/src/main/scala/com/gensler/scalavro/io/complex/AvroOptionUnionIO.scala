package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive.{ AvroLongIO, AvroNullIO }
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.AvroUnion
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }
import com.gensler.scalavro.io.AvroTypeIO.Implicits._
import com.gensler.scalavro.util.Union
import com.gensler.scalavro.util.Union._

import org.apache.avro.io.BinaryEncoder

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import java.io.{ InputStream, OutputStream }

private[scalavro] case class AvroOptionUnionIO[U <: Union.not[_]: TypeTag, T <: Option[_]: TypeTag](
    avroType: AvroUnion[U, T]) extends AvroUnionIO[U, T] {

  // IMPORTANT:
  // null is the 0th index in the union, per AvroType.fromType
  val (nullIndex, nonNullIndex) = (0L, 1L)

  val TypeRef(_, _, List(innerType)) = typeOf[T]

  val innerAvroType = avroType.memberAvroTypes.find { at => innerType <:< at.tag.tpe }.get

  protected[scalavro] def asGeneric[X <: T: TypeTag](obj: X) =
    asGenericHelper(obj)(typeTag[X], innerAvroType.tag)

  private def asGenericHelper[X <: T: TypeTag, A: TypeTag](obj: X) = obj match {
    case Some(value) => innerAvroType.asInstanceOf[AvroType[A]].asGeneric(value.asInstanceOf[A])
    case None        => null
  }

  def write[X <: T: TypeTag](obj: X, encoder: BinaryEncoder) = {
    AvroLongIO.write(if (obj.isDefined) nonNullIndex else nullIndex, encoder)
    writeHelper(obj, encoder)(typeTag[X], innerAvroType.tag)
    encoder.flush
  }

  def writeHelper[X <: T: TypeTag, A: TypeTag](obj: X, encoder: BinaryEncoder) =
    obj match {
      case Some(value) => innerAvroType.asInstanceOf[AvroType[A]].write(value.asInstanceOf[A], encoder)
      case None        => AvroNullIO.write((), encoder)
    }

  def read(stream: InputStream) = Try {
    readHelper(stream)(innerAvroType.tag).asInstanceOf[T]
  }

  def readHelper[A: TypeTag](stream: InputStream) = {
    val index = AvroLongIO.read(stream).get
    if (index == nonNullIndex) Some(innerAvroType.read(stream).get.asInstanceOf[A])
    else if (index == nullIndex) None
    else throw new AvroDeserializationException[T](
      detailedMessage = "Encountered an index that was not zero or one: [%s]" format index
    )
  }
}