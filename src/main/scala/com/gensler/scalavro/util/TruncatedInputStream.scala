package com.gensler.scalavro.util

import java.io.InputStream

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