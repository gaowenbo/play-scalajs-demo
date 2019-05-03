package actors

import play.api.libs.json.{JsPath, Reads, Writes}
import stocks._
import play.api.libs.functional.syntax._
object Messages {

  case class Stocks(stocks: Set[Stock]) {
    require(stocks.nonEmpty, "Must specify at least one stock!")
  }

  case class WatchStocks(symbols: Set[StockSymbol]) {
    require(symbols.nonEmpty, "Must specify at least one symbol!")
  }

  case class UnwatchStocks(symbols: Set[StockSymbol]) {
    require(symbols.nonEmpty, "Must specify at least one symbol!")
  }


//  object JsonConvertor {
//    implicit var messageReads: Reads[Chat] = (
//      (JsPath \ "sender").read[String] and
//        (JsPath \ "content").read[String] and
//        (JsPath \ "status").read[String]
//      )(Chat)
//    implicit var messageWrites: Writes[Chat] = (
//      (JsPath \ "sender").write[String] and
//        (JsPath \ "content").write[String] and
//        (JsPath \ "status").write[String]
//      )(unlift(Chat.unapply))
//  }
}


