package com.gensler.scalavro.types.primitive

import com.gensler.scalavro.types.AvroPrimitiveType
import com.gensler.scalavro.error.AvroDeserializationException
import com.gensler.scalavro.util.TruncatedInputStream
import scala.util.Try
import spray.json._
import java.io.{ InputStream, InputStreamReader, OutputStream, OutputStreamWriter, DataOutputStream, ByteArrayOutputStream }
import java.nio.charset.Charset

trait AvroString extends AvroPrimitiveType[String] {

  val typeName = "string"

  def write(value: String, stream: OutputStream) = {
    val buffer = new ByteArrayOutputStream
    val writer = new OutputStreamWriter(buffer, Charset.forName("UTF-8"))
    writer.write(value, 0, value.length)
    writer.close

    AvroLong.write(buffer.size, stream) // num bytes of utf-8 that follow
    buffer writeTo stream               // bare utf-8 data
  }

  def writeAsJson(value: String): JsValue = ???

  def read(stream: InputStream) = Try {
    val length = AvroLong.read(stream).get
    println(length)

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

  def readFromJson(json: JsValue) = Try { ???.asInstanceOf[String] }

}

object AvroString extends AvroString