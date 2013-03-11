package com.gensler.scalavro

trait AvroOrder { def value(): String }

object AvroOrder {
  case object Ascending extends AvroOrder { val value = "ascending" }
  case object Descending extends AvroOrder { val value = "descending" }
  case object Ignore extends AvroOrder { val value = "ignore" }
}
