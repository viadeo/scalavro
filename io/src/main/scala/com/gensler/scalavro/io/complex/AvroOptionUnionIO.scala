package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive.{AvroLongIO, AvroNullIO}
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.AvroUnion
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}
import com.gensler.scalavro.io.AvroTypeIO.Implicits._

import com.gensler.scalavro.util.Union
import com.gensler.scalavro.util.Union._

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericArray, GenericDatumWriter, GenericDatumReader}
import org.apache.avro.io.{EncoderFactory, DecoderFactory}

import scala.util.{Try, Success, Failure}
import scala.reflect.runtime.universe._

import java.io.{InputStream, OutputStream}

private[scalavro] case class AvroOptionUnionIO[
  U <: Union.not[_]: TypeTag,
  T <: Option[_]: TypeTag
](
  avroType: AvroUnion[U, T]
) extends AvroUnionIO[U, T] {

  val TypeRef(_, _, List(innerType)) = typeOf[T]

  val innerAvroType = avroType.memberAvroTypes.find { at => innerType <:< at.tag.tpe }.get

  protected[scalavro] def asGeneric[X <: T : TypeTag](obj: X) =
    asGenericHelper(obj)(typeTag[X], innerAvroType.tag)

  private def asGenericHelper[X <: T : TypeTag, A: TypeTag](obj: X) = obj match {
    case Some(value) => innerAvroType.asInstanceOf[AvroType[A]].asGeneric(value.asInstanceOf[A])
    case None => Unit
  }

  protected[scalavro] def fromGeneric(obj: Any): T = {
    println("AvroOptionUnionIO.fromGeneric -- received an object of type [%s]" format obj.getClass.getName)
    ??? // throws NotImplementedException
  }

  def write[X <: T : TypeTag](obj: X, stream: OutputStream) = {
    AvroLongIO.write(if (obj.isDefined) 0L else 1L, stream)
    writeHelper(obj, stream)(typeTag[X], innerAvroType.tag)
  }

  def writeHelper[X <: T : TypeTag, A: TypeTag](obj: X, stream: OutputStream) =
    obj match {
      case Some(value) => innerAvroType.asInstanceOf[AvroType[A]].write(value.asInstanceOf[A], stream)
      case None => AvroNullIO.write((), stream)
    }

  def read(stream: InputStream) = Try {
    readHelper(stream)(innerAvroType.tag).asInstanceOf[T]
  }

  def readHelper[A: TypeTag](stream: InputStream) = {
    val index = AvroLongIO.read(stream).get
    if (index == 0) Some(innerAvroType.read(stream).get.asInstanceOf[A])
    else if (index == 1) None
    else throw new AvroDeserializationException[T]
  }
}