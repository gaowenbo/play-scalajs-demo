package views

import controllers.routes

object ConvertHtml {
    def getHtml =
      {
        var js = scalajs.html.scripts("client", routes.Assets.versioned(_).toString, name => getClass.getResource(s"/public/$name") != null)
        println(js)
        "<!DOCTYPE html>" +
          s"""<html>
            <body>
              <div>Hello World!</div>

              <form action="#">
                <label for="name">Name: </label><input id="name" type="text" />
                <input id="join" type="button" value="Join!"/>
              </form>

              <form action="#">
                <label for="message">Say something: </label><input id="message" type="text" />
                <input id="send" type="button" value="Send" disabled="true"/>
              </form>

              <div id="playground"/>
              ${js}
            </body>
          </html>"""
      }

}
