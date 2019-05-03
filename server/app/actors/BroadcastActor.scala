package actors


import akka.actor._
import com.wenbo.hello.shared.SharedMessages._

class BroadcastActor(out: ActorRef, user: Option[String]) extends Actor with ActorLogging {
  import com.hypertino.binders.json.JsonBinders._
  def receive: Receive = {
    case Join(s) =>
      out ! Chat(s, "", "Join").toJson
    case Broadcast(s, c) =>
      out ! Chat(s, c, "Broadcast").toJson
    case Leave(s) =>
      out ! Chat(s, "", "Leave").toJson
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
