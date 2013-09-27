package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive.AvroLongIO
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.AvroUnion
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }
import com.gensler.scalavro.util.ReflectionHelpers

import com.gensler.scalavro.util.Union
import com.gensler.scalavro.util.Union._

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import java.io.{ InputStream, OutputStream }

private[scalavro] case class AvroClassUnionIO[U <: Union.not[_]: TypeTag, T: TypeTag](
    avroType: AvroUnion[U, T]) extends AvroUnionIO[U, T] {

  protected[scalavro] def write[X <: T: TypeTag](
    obj: X,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    val staticTypeOfObj = typeOf[X]
    val runtimeTypeOfObj = ReflectionHelpers.classLoaderMirror.staticClass(obj.getClass.getName).toType
    val objTypeTag = ReflectionHelpers.tagForType(runtimeTypeOfObj)

    avroType.memberAvroTypes.indexWhere {
      at => staticTypeOfObj <:< at.tag.tpe || runtimeTypeOfObj <:< at.tag.tpe
    } match {
      case -1 => throw new AvroSerializationException(obj)
      case index: Int => {
        AvroLongIO.write(index.toLong, encoder, references, false)
        val memberType = avroType.memberAvroTypes(index).asInstanceOf[AvroType[X]]
        memberType.io.write(obj, encoder, references, false)(objTypeTag.asInstanceOf[TypeTag[X]])
        encoder.flush
      }
    }
  }

  def read(decoder: BinaryDecoder) = {
    val index = AvroLongIO.read(decoder)
    val memberType = avroType.memberAvroTypes(index.toInt).asInstanceOf[AvroType[T]]
    memberType.io.read(decoder)
  }

}