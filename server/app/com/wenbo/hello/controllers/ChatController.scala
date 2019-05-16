package com.wenbo.hello.controllers

import actors.{BroadcastActor, ChatClientActor}
import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import akka.stream.{ActorMaterializer, KillSwitches, Materializer, UniqueKillSwitch}
import javax.inject.{Inject, Named}
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, RequestHeader, WebSocket}
import views.ConvertHtml

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class ChatController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val logger = play.api.Logger(getClass)
  protected val amMap = new scala.collection.mutable.HashMap[String, (ActorSystem, Materializer, Flow[Message, Message, UniqueKillSwitch])]

  def chatPage = Action {
    Ok(ConvertHtml.getHtml).as(HTML)
  }

  def getFromMap(str: String) = {
    this.synchronized({
      if (!amMap.contains(str)) {
        implicit val actorSystem = ActorSystem("akkasss" + str)
        implicit val mat = ActorMaterializer()
        var (hubSink: Sink[Message, NotUsed], hubSource: Source[Message, NotUsed]) = MergeHub.source[Message](16).toMat(BroadcastHub.sink(256))(Keep.both).run()
        var killSwitchFlow: Flow[Message, Message, UniqueKillSwitch] = {
          Flow.fromSinkAndSource(hubSink, hubSource).joinMat((KillSwitches.singleBidi[Message, Message]))(Keep.right).backpressureTimeout(3 second)
        }
        amMap.put(str, (actorSystem, mat, killSwitchFlow))
      }
      amMap.getOrElse(str, null)
    })
  }

  def chat: WebSocket = WebSocket.acceptOrResult[String, String] {
    case rh if sameOriginCheck(rh) =>
      var user = rh.queryString("user").headOption
      var room = rh.queryString("room").headOption
      implicit val (actorSystem, mat, killSwitchFlow): (ActorSystem, Materializer,  Flow[Message, Message, UniqueKillSwitch]) = getFromMap(room.get)
      var chatClient = ActorFlow.actorRef[String, Message](out => ChatClientActor.props(out, user))
      var broadcast =  ActorFlow.actorRef[Message, String](out => BroadcastActor.props(out, user))
      implicit val ec = actorSystem.dispatcher
      var flow = chatClient.viaMat(killSwitchFlow)(Keep.right).viaMat(broadcast)(Keep.right)
      Future.successful(flow).map { flow =>
        Right(flow)
      }.recover {
        case e: Exception =>
          logger.error("cannot create websocket", e)
          val jsError = Json.obj("error" -> "cannot create websocket")
          val result = InternalServerError(jsError)
          Left(result)
      }

    case rejected =>
      logger.error(s"Request ${rejected}")
      Future.successful {
        Left(Forbidden("forbidden"))
      }
  }
  /**
    * Checks that the WebSocket comes from the same origin.  This is necessary to protect
    * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
    *
    * See https://tools.ietf.org/html/rfc6455#section-1.3 and
    * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
    */
  def sameOriginCheck(rh: RequestHeader): Boolean = {
    rh.headers.get("Origin") match {
      case Some(originValue) if originMatches(originValue) =>
        logger.debug(s"originCheck: originValue = $originValue")
        true

      case Some(badOrigin) =>
        logger.error(s"originCheck: rejecting request because Origin header value ${badOrigin} is not in the same origin")
        false

      case None =>
        logger.error("originCheck: rejecting request because no Origin header found")
        false
    }
  }

  /**
    * Returns true if the value of the Origin header contains an acceptable value.
    *
    * This is probably better done through configuration same as the allowedhosts filter.
    */
  def originMatches(origin: String): Boolean = {
    //    origin.contains("localhost:9000") || origin.contains("localhost:19001")
    true
  }
}