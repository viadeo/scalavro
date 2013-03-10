package com.gensler.scalavro.error

import scala.reflect.runtime.universe._

class AvroSerializationException[T: TypeTag](obj: T)
  extends Exception(
    "A problem occurred while attempting to serialize a value of type [" + typeOf[T] + "].\n" +
    "Attempted to serialize the value: " + obj.toString
  )
