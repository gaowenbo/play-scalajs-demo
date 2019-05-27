package actors

import akka.actor._
import com.wenbo.hello.shared.SharedMessages._

class ChatRoomActor(out: ActorRef, room: Option[String]) extends Actor with ActorLogging {
  def receive: Receive = {
    case msg: Message => {
        out ! msg
    }
  }
}
object ChatRoomActor{
  def props(out: ActorRef, room: Option[String]) = Props(classOf[ChatRoomActor], out, room)
}

