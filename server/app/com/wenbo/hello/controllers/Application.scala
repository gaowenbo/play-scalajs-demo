package com.wenbo.hello.controllers

import javax.inject._
import com.wenbo.hello.shared.SharedMessages

import play.api._
import play.api.mvc._
import models._
import akka.stream.Materializer
import play.api.libs.streams._
import play.api.mvc.WebSocket._
import akka.actor._

import scala.concurrent.{ExecutionContext, Future}



@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val logger = play.api.Logger(getClass)

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
