package com.gensler.scalavro

trait JsonSchemifiable {
  def schema(): spray.json.JsValue
}