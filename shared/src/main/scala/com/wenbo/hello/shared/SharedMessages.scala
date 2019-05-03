package com.wenbo.hello.shared

object SharedMessages {
  def itWorks = "It wworkrrs!"

  def itWorks2 = "It wworkrrs!"


  case class Join(s: String) {}

  case class Broadcast(s: String, c: String){}

  case class Leave(s:String) {
  }
  case class Chat(sender: String, content: String, status: String)

}


