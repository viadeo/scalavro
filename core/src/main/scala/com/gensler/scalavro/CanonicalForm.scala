package com.gensler.scalavro

trait CanonicalForm {
  def parsingCanonicalForm(): spray.json.JsValue
}