package actors

import actors.Messages.{Broadcast, Chat, Join, Leave}
import akka.actor._
import play.api.libs.json.JsValue

class ChatClientActor(out: ActorRef, user: Option[String]) extends Actor with ActorLogging {
  import actors.Messages.JsonConvertor._

  def receive: Receive = {
    case msg: JsValue => {
      var chat = msg.as[Chat]
      if (chat.sender == user.get && chat.status == "Chat") {
        out ! Broadcast(chat.sender, chat.content)
      }
    }
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    user match {
      case Some(u) => out ! Join(u)
      case None =>
        log.info(s"No user name ${out.path}")
        self ! PoisonPill
    }
  }

  override def postStop(): Unit = {
    user match {
      case Some(u) => out ! Leave(u)
      case None => log.info(s"Stop ${out.path}")
    }
    self ! PoisonPill
  }
}

object ChatClientActor{
  def props(out: ActorRef, user: Option[String]) = Props(classOf[ChatClientActor], out, user)
}
