package com.gensler.scalavro.error

import scala.reflect.runtime.universe._

class AvroSerializationException[T: TypeTag](obj: T, cause: java.lang.Throwable = null)
  extends Exception(
    "A problem occurred while attempting to serialize a value of type [" + typeOf[T] + "].\n" +
      "Attempted to serialize the value: " + obj.toString,
    cause
  )
