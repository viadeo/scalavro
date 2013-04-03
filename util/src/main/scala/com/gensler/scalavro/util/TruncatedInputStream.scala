package com.gensler.scalavro.util

import java.io.InputStream

/**
  * A finite view of an underlying [[java.io.InputStream]].
  *
  * @constructor
  * @param stream    the underlying input stream
  * @param maxLength the maximum number of bytes that can be read from this
  *                  input stream
  */
class TruncatedInputStream(stream: InputStream, maxLength: Long) extends InputStream {

  var bytesRead = 0L

  override def read() = {
    if (bytesRead >= maxLength) -1
    else {
      val charCode = stream.read()
      bytesRead += 1
      charCode
    }
  }

}