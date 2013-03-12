package com.gensler.scalavro.types
 
import scala.reflect.runtime.universe._

abstract class AvroComplexType[T: TypeTag] extends AvroType[T] {
  final val isPrimitive = false

  override def toString(): String = {
    "%s[%s]".format(getClass.getSimpleName, typeOf[T])
  }

}
