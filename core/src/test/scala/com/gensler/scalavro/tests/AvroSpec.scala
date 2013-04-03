package com.gensler.scalavro.tests

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import spray.json.{ JsValue, PrettyPrinter }
import org.slf4j.{ Logger, LoggerFactory }

trait AvroSpec extends FlatSpec with ShouldMatchers {

  protected val log = LoggerFactory.getLogger(getClass.getName)

  protected def prettyPrint(json: JsValue) {
    val buff = new java.lang.StringBuilder
    PrettyPrinter.print(json, buff)
    log debug buff.toString
  }

}