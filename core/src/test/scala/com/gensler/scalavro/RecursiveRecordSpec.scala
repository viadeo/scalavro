package com.gensler.scalavro.test

import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.AvroRecord

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

class RecursiveRecordSpec extends AvroSpec {

  "The AvroType object" should "create an AvroType for recursive case classes" in {
    val Success(listAvroType) = AvroType.fromType[SinglyLinkedStringList]
    listAvroType.isInstanceOf[AvroRecord[SinglyLinkedStringList]] should equal (true)
    typeOf[listAvroType.scalaType] =:= typeOf[SinglyLinkedStringList] should be (true)
  }

  it should "produce a schema for a record derived from a recursively defined case class" in {
    AvroType[SinglyLinkedStringList].schema.toString should equal ("""
{
  "name": "com.gensler.scalavro.test.SinglyLinkedStringList",
  "type": "record",
  "fields": [{
    "name": "data",
    "type": "string"
  }, {
    "name": "next",
    "type": ["null", ["com.gensler.scalavro.test.SinglyLinkedStringList", {
      "name": "com.gensler.scalavro.Reference",
      "type": "record",
      "fields": [{
        "name": "id",
        "type": "long"
      }]
    }]]
  }]
}
""".replaceAll("\\s", "")
    )
  }

}