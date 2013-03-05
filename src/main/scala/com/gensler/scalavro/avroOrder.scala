package com.gensler.scalavro

trait AvroOrder {
  def value(): String
}

case object AvroAscending extends AvroOrder { val value = "ascending" }
case object AvroDescending extends AvroOrder { val value = "descending" }
case object AvroIgnore extends AvroOrder { val value = "ignore" }
