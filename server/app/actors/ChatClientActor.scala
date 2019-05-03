package actors

import com.wenbo.hello.shared.SharedMessages._
import akka.actor._
import play.api.libs.json.JsValue

class ChatClientActor(out: ActorRef, user: Option[String]) extends Actor with ActorLogging {
  import com.hypertino.binders.json.JsonBinders._
  def receive: Receive = {
    case msg: String => {
      var chat = msg.parseJson[Chat]
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
