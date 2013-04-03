package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroString
import com.gensler.scalavro.util.TruncatedInputStream
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, InputStreamReader, OutputStream, OutputStreamWriter, DataOutputStream, ByteArrayOutputStream}
import java.nio.charset.Charset


object AvroStringIO extends AvroStringIO

trait AvroStringIO extends AvroTypeIO[String] {

  def avroType = AvroString

  def write(value: String, stream: OutputStream) = {
    val buffer = new ByteArrayOutputStream
    val writer = new OutputStreamWriter(buffer, Charset.forName("UTF-8"))
    writer.write(value, 0, value.length)
    writer.close

    AvroLongIO.write(buffer.size, stream) // num bytes of utf-8 that follow
    buffer writeTo stream               // bare utf-8 data
  }

  def read(stream: InputStream) = Try {
    val length = AvroLongIO.read(stream).get

    val reader = new InputStreamReader(
      new TruncatedInputStream(stream, length),
      Charset.forName("UTF-8")
    )

    val stringBuilder = new StringBuilder()

    var continueReading = true
    while (continueReading) {
      val charCode: Int = reader.read
      if (charCode < 0) continueReading = false
      else stringBuilder.append(charCode.toChar)
    }

    stringBuilder.toString
  }

}