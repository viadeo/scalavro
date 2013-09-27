package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive.AvroLongIO
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.AvroUnion
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }
import com.gensler.scalavro.util.Union
import com.gensler.scalavro.util.Union._

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import java.io.{ InputStream, OutputStream }

private[scalavro] case class AvroEitherUnionIO[U <: Union.not[_]: TypeTag, T <: Either[_, _]: TypeTag](
    avroType: AvroUnion[U, T]) extends AvroUnionIO[U, T] {

  val TypeRef(_, _, List(leftType, rightType)) = typeOf[T]
  val leftAvroType = avroType.memberAvroTypes.find { at => leftType <:< at.tag.tpe }.get
  val rightAvroType = avroType.memberAvroTypes.find { at => rightType <:< at.tag.tpe }.get

  protected[scalavro] def write[X <: T: TypeTag](
    obj: X,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    AvroLongIO.write(if (obj.isLeft) 0L else 1L, encoder)
    writeHelper(obj, encoder, references, topLevel)(typeTag[X], leftAvroType.tag, rightAvroType.tag)
    encoder.flush
  }

  protected[this] def writeHelper[X <: T: TypeTag, A: TypeTag, B: TypeTag](
    obj: X,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean) = obj match {

    case Left(value)  => leftAvroType.asInstanceOf[AvroType[A]].io.write(value.asInstanceOf[A], encoder, references, false)
    case Right(value) => rightAvroType.asInstanceOf[AvroType[B]].io.write(value.asInstanceOf[B], encoder, references, false)
  }

  protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = {

    readHelper(decoder, references, topLevel)(leftAvroType.tag, rightAvroType.tag).asInstanceOf[T]
  }

  def readHelper[A: TypeTag, B: TypeTag](
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = {

    (AvroLongIO read decoder) match {
      case 0 => Left(leftAvroType.io.read(decoder, references, false).asInstanceOf[A])
      case 1 => Right(rightAvroType.io.read(decoder, references, false).asInstanceOf[B])
      case _ => throw new AvroDeserializationException[T]
    }
  }
}