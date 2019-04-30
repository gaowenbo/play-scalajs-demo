package com.wenbo.hello

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.{HTMLButtonElement, HTMLInputElement, HTMLTextAreaElement}

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
}
