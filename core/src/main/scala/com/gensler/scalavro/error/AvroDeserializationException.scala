package com.gensler.scalavro.error

import scala.reflect.runtime.universe._

class AvroDeserializationException[T: TypeTag](cause: java.lang.Throwable = null)
  extends Exception(
    "A problem occurred while attempting to deserialize a value of type [" + typeOf[T] + "].",
    cause
  )
