package com.wenbo.hello.controllers

import javax.inject._

import com.wenbo.hello.shared.SharedMessages
import play.api.mvc._

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action {
    Ok(views.html.index(SharedMessages.itWorks))
  }

  def index2(st: String) = Action {
    Ok(views.html.index(SharedMessages.itWorks))
  }

  def convert = Action {
    Ok("<html>" +

      "</html>")
  }


}
