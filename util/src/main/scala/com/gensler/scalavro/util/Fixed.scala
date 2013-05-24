package com.gensler.scalavro.util

/**
  * The type of fixed-length data.
  *
  * @param length The length of the serialized form of this Fixed data type.
  */
abstract class FixedData {

  /**
    * Returns the value of this Fixed data element as a sequence of bytes.
    * The size of this sequence must be exactly the companion object's
    * `length` field.
    */
  def bytes(): Seq[Byte]
}

/**
  * The type of fixed-length data companion objects.
  */
trait FixedDataCompanion {

  /**
    * The length of the serialized form of this Fixed data type.
    */
  val length: Int

}
