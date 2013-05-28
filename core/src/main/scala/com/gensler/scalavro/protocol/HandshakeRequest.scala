package com.gensler.scalavro.protocol

case class HandshakeRequest(
  clientHash: MD5,
  clientProtocol: Option[String],
  serverHash: MD5,
  meta: Map[String, Seq[Byte]])
