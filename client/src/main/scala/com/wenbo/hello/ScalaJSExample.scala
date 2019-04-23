package com.wenbo.hello

import com.wenbo.hello.shared.SharedMessages
import org.scalajs.dom
import org.scalajs.dom.{Event, html, raw}
import org.scalajs.dom.raw.{Element, HTMLTextAreaElement}



object ScalaJSExample {

  def main(args: Array[String]): Unit = {
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
