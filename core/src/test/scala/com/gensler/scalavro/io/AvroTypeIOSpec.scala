package com.gensler.scalavro.io.test

import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import com.gensler.scalavro.types._
import com.gensler.scalavro.types.primitive._

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive._

class AvroTypeIOSpec extends FlatSpec with Matchers {

  it should "resolve AvroTypeIO objects for primitive types implicitly" in {
    AvroBoolean.io should equal (AvroBooleanIO)
    AvroByte.io should equal (AvroByteIO)
    AvroBytes.io should equal (AvroBytesIO)
    AvroChar.io should equal (AvroCharIO)
    AvroDouble.io should equal (AvroDoubleIO)
    AvroFloat.io should equal (AvroFloatIO)
    AvroInt.io should equal (AvroIntIO)
    AvroLong.io should equal (AvroLongIO)
    AvroNull.io should equal (AvroNullIO)
    AvroShort.io should equal (AvroShortIO)
    AvroString.io should equal (AvroStringIO)
    AvroXml.io should equal (AvroXmlIO)

    AvroJavaBoolean.io should equal (AvroJavaBooleanIO)
    AvroJavaByte.io should equal (AvroJavaByteIO)
    //    AvroJavaBytes.io should equal (AvroJavaBytesIO)
    AvroJavaCharacter.io should equal (AvroJavaCharacterIO)
    AvroJavaDouble.io should equal (AvroJavaDoubleIO)
    AvroJavaFloat.io should equal (AvroJavaFloatIO)
    AvroJavaInteger.io should equal (AvroJavaIntegerIO)
    AvroJavaLong.io should equal (AvroJavaLongIO)
    AvroJavaShort.io should equal (AvroJavaShortIO)
    //    AvroW3Xml.io should equal (AvroW3XmlIO)
  }

}