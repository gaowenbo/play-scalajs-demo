package actors

import actors.Messages._
import akka.actor._
import play.api.libs.json._

class BroadcastActor(out: ActorRef, user: Option[String]) extends Actor with ActorLogging {
  import actors.Messages.JsonConvertor._
  def receive: Receive = {
    case Join(s) =>
      out ! Json.toJson(Chat(s, "", "JOIN"))
    case Broadcast(s, c) =>
      out ! Json.toJson(Chat(s, c, "Broadcast"))
    case Leave(s) =>
      out ! Json.toJson(Chat(s, "", "Leave"))
      user match {
        case Some(u) =>
          if (u == s) {
            out ! PoisonPill
            self ! PoisonPill
          }
        case None =>
          self ! PoisonPill
      }
  }
}

object BroadcastActor {
  def props(out: ActorRef, user: Option[String]): Props = Props(classOf[BroadcastActor], out, user)
}
