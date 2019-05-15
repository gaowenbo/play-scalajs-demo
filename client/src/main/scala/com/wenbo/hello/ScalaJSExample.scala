package com.wenbo.hello

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.{Element, HTMLTextAreaElement}
import com.wenbo.chat.ChatPage
import scala.scalajs.js.timers.setTimeout

object ScalaJSExample {

  def main(args: Array[String]): Unit = {
    var route = (document.location.href.substring(document.location.protocol.size + 2 + document.location.host.size))
    if (route == "/" || route == "" || route.startsWith("/?")) {
      //创建一个标签
      var input = dom.document.createElement("textarea").asInstanceOf[HTMLTextAreaElement]
      dom.document.body.appendChild(input)

      var button = dom.document.createElement("button")
      button.innerHTML = "批量数据库"
      var changeText = (e: Event) => {
        var value = input.value
        var realDb = "zhiyunshan_trade_order"
        //      var result = (1 to 32).map(i => {
        //        value.replaceAll("`[a-zA-Z0-9_]+`\\.", "`" + realDb + i + "`.")
        //      }).mkString(",")
        var result = (1 to 32).map(i => {
          value.replaceAll("pay", realDb + "" + i)
        }).mkString(",")
        input.value = result
      }
      button.addEventListener("click", changeText);
      dom.document.body.appendChild(button)
      //connectWS
    } else if (route == "/convert" || route.startsWith("/convert?")) {
      println(route)
      ScalaJSExample2.run
    } else if (route == "/chatPage" || route.startsWith("/chatPage?")) {
      println(route)
      ChatPage.run
    }
  }

  private lazy val wsURL = s"ws://${document.location.host}/ws"
  private lazy val wsURL2 = s"ws://${document.location.host}/hello"

  lazy val socket = new WebSocket(wsURL)

  def connectWS() {
    socket.onmessage = {
      (e: MessageEvent) =>
        println(e.data.toString)

//        appendPar(dom.document.body, e.data.toString)
//        val message = Json.parse(e.data.toString)
//        message.validate[AdapterMsg] match {
//          case JsSuccess(AdapterRunning(logReport), _) =>
//            changeIsRunning(true)
//            newLogEntries(logReport)
//          case JsSuccess(AdapterNotRunning(logReport), _) =>
//            changeIsRunning(false)
//            logReport.foreach { lr =>
//              changeLastLogLevel(lr)
//              newLogEntries(lr)
//            }
//          case JsSuccess(LogEntryMsg(le), _) =>
//            newLogEntry(le)
//          case JsSuccess(RunStarted, _) =>
//            changeIsRunning(true)
//          case JsSuccess(RunFinished(logReport), _) =>
//            changeIsRunning(false)
//            changeLastLogLevel(logReport)
//          case JsSuccess(other, _) =>
//            println(s"Other message: $other")
//          case JsError(errors) =>
//            errors foreach println
//        }
    }
    socket.onerror = { (e: Event) =>
      println(s"exception with websocket: ${e}!")
      socket.close(0, "ddddd")
    }
    socket.onopen = { (_: Event) =>
      println("websocket open!")
//      clearLogData()
    }
    socket.onclose = { (e: CloseEvent) =>
      println("closed socket" + e.reason)
      setTimeout(1000) {
        connectWS() // try to reconnect automatically
      }
    }
  }

  def init = print("d")

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = dom.document.createElement("p")
    val textNode = dom.document.createTextNode(text)


    targetNode.appendChild(parNode)
  }

  val digits = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x')

  def shortCode(value: String): String = {
    if (null == value || value.length == 0) return ""
    var rv = 0xcbf29ce484222325L
    val len = value.length
    var i = 0
    while ( {
      i < len
    }) {
      rv ^= value.charAt(i)
      rv *= 0x100000001b3L

      {
        i += 1; i
      }
    }
    var m = ""
    val negative = rv < 0
    if (!negative) rv = -rv
    while ( {
      rv <= -34
    }) {
      m += digits((-rv % 34).toInt)
      rv = rv / 34
    }
    m += digits((-rv).toInt)
    if (negative) m = 'y' + m
    else m = 'z' + m
    m


  }

  //  @JSExportTopLevel("encode")
  def encode(s: String): String = shortCode(s)


//
//    def selectedFile(e: ReactEventI) = {
//      val reader = new dom.FileReader()
//
//      reader.readAsText(dom.files.item(0))
//      reader.onload(
//      )
//    }
//
////  @JSExportTopLevel("convertSql")
//  def convertSql(): Unit = {
//    var value = $("#msconvertsqlinput").value().toString
//    val sb = new StringBuffer
//    val p = Pattern.compile("`[a-zA-Z0-9]+(_[a-zA-Z0-9]+)+`")
//    val m = p.matcher(value)
//    while ( {
//      m.find
//    }) {
//      if (!m.group.equals("ROW_FORMAT") && !m.group.equals("AUTO_INCREMENT") && !m.group.equals("CURRENT_TIMESTAMP") ) {
//
//        m.appendReplacement(sb, "`" + shortCode(m.group.toLowerCase.substring(1, m.group.length -  1)) + "`")
//      }
//    }
//    m.appendTail(sb)
//    $("#msconvertsqlinput").value(sb.toString)
//  }
//
////  @JSExportTopLevel("convert")
//  def convert(): Unit = {
//    var value = $("#msconvertinput").value().toString
//    $("#msconvertinput").value(shortCode(value))
//  }
//
//
//
////  @JSExportTopLevel("decode")
//  def decode(s: String): String = {
//    var str = s;
//    $("#dict").value.toString.split("\n").distinct.map(
//      i => (shortCode(i), i)
//    ).foreach(m => {
//      str = str.replaceAll(m._1, m._2)
//    })
//    str
//  }
}
