package com.wenbo.hello

import com.wenbo.hello.shared.SharedMessages.Chat
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.{HTMLButtonElement, HTMLInputElement, HTMLParagraphElement, HTMLTextAreaElement}

import scala.scalajs.js.JSON
import scala.scalajs.js.timers.setTimeout

object ScalaJSExample2 {

    var joinButton = dom.document.getElementById("join").asInstanceOf[HTMLButtonElement]
    var sendButton = dom.document.getElementById("send").asInstanceOf[HTMLButtonElement]

    def run = {
        var nameField = dom.document.getElementById("name").asInstanceOf[HTMLInputElement]
        import com.hypertino.binders.json.JsonBinders._
        println(List(1, 3, 2).toJson.toString)
        joinButton.onclick = {event =>
            joinChat(nameField.value)
            event.preventDefault()
        }
        nameField.focus()
        nameField.onkeypress = {event =>
          if (event.keyCode == 13) {
              joinButton.click()
              event.preventDefault()
          }
        }
    }

  def getWebsocketUrl(document: html.Document, name: String): String = {
    var wsProtocol = if (dom.document.location.protocol == "https") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/chatroom?user=$name"
  }

  def joinChat(name: String) = {
      joinButton.disabled = true;
      var playground = dom.document.getElementById("playground")
      playground.innerHTML = s"${name}登录中。。。";

      var chat = new WebSocket(getWebsocketUrl(dom.document, name), "wss")
      chat.onopen = {e =>
        playground.insertBefore(p("连接成功！"), playground.firstChild)
        sendButton.disabled = false
        var messageField = dom.document.getElementById("message").asInstanceOf[HTMLInputElement]
        messageField.focus()
        messageField.onkeypress = {event =>
          if (event.keyCode == 13) {
            sendButton.click()
            event.preventDefault()
          }
        }

        sendButton.onclick = {event =>
          import com.hypertino.binders.json.JsonBinders._
          chat.send(Chat(name, messageField.value, "Chat").toJson)
          messageField.value = ""
          messageField.focus()
          event.preventDefault()
        }

      }

      chat.onerror = {e =>
        playground.insertBefore(p(s"failed : code ${e.asInstanceOf[ErrorEvent].colno}"), playground)
        joinButton.disabled = false;
        sendButton.disabled = true;
      }

      chat.onmessage = {e =>
        println(e.data.toString)
        import com.hypertino.binders.json.JsonBinders._
        var message = e.data.toString.parseJson[Chat]
        message match {
          case Chat(sender, _, "Join") =>
            playground.insertBefore(p(s"欢迎用户 ${sender} 登录！"), playground.firstChild)
          case Chat(sender, content, "Broadcast") =>
            playground.insertBefore(p(s"${sender}:${content}"), playground.firstChild)
          case Chat(sender, _, "Leave") =>
            playground.insertBefore(p(s"用户 ${sender} 退出"), playground.firstChild)
          case _ => {
            println("not supported")
          }
        }
        e
      }
    }

  def p(text: String) = {
    var p = dom.document.createElement("p").asInstanceOf[HTMLParagraphElement]
    p.innerHTML = text
    p
  }
}
