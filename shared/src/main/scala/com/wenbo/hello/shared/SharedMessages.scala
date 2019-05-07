package com.wenbo.hello.shared

object SharedMessages {
  def itWorks = "It wworkrrs!"

  def itWorks2 = "It wworkrrs!"

  sealed trait Message

  case class Join(s: String) extends Message

  case class Broadcast(s: String, c: String)extends Message

  case class Leave(s:String) extends Message
  case class Chat(sender: String, content: String, status: String)

}


