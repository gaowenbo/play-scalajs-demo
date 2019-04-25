package com.wenbo.hello.controllers

import javax.inject._
import com.wenbo.hello.shared.SharedMessages
import actors._
import akka.NotUsed
import akka.actor._
import akka.pattern.ask
import akka.stream.scaladsl._
import akka.util.Timeout
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.DurationConversions
import scala.concurrent.duration.Duration
import scala.concurrent.duration.span
import scala.concurrent.duration.TimeUnit
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Application @Inject()(@Named("userParentActor") userParentActor: ActorRef,
cc: ControllerComponents)
(implicit ec: ExecutionContext)
extends AbstractController(cc) with SameOriginCheck {

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

  private val helloSource = Source.single("Hello!")
  private val logSink = Sink.foreach { s: String => logger.info(s"received: $s") }

  def hello: WebSocket = WebSocket.accept[String, String] { _ =>
    val closeAfterMessage = false

//    close.getOrElse(false)
    Flow.fromSinkAndSource(
      logSink,
      // server close connection after sending message
      if (closeAfterMessage) helloSource
      // keep connection open
      else helloSource.concat(Source.maybe)
    )
  }



  def ws: WebSocket = WebSocket.acceptOrResult[JsValue, JsValue] {
    case rh if sameOriginCheck(rh) =>
      wsFutureFlow(rh).map { flow =>
        Right(flow)
      }.recover {
        case e: Exception =>
          logger.error("Cannot create websocket", e)
          val jsError = Json.obj("error" -> "Cannot create websocket")
          val result = InternalServerError(jsError)
          Left(result)
      }

    case rejected =>
      logger.error(s"Request ${rejected} failed same origin check")
      Future.successful {
        Left(Forbidden("forbidden"))
      }
  }

  /**
    * Creates a Future containing a Flow of JsValue in and out.
    */
  private def wsFutureFlow(request: RequestHeader): Future[Flow[JsValue, JsValue, NotUsed]] = {
    // Use guice assisted injection to instantiate and configure the child actor.
    implicit val timeout = Timeout(1.second) // the first run in dev can take a while :-(
    val future: Future[Any] = userParentActor ? UserParentActor.Create(request.id.toString)
    val futureFlow: Future[Flow[JsValue, JsValue, NotUsed]] = future.mapTo[Flow[JsValue, JsValue, NotUsed]]
    futureFlow
  }

}
trait SameOriginCheck {

  def logger: Logger

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