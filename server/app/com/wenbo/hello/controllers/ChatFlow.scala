package com.wenbo.hello.controllers

import akka.NotUsed
import akka.http.scaladsl.model.ws.Message
import akka.stream.{KillSwitches, Materializer, UniqueKillSwitch}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import play.api.libs.json.JsValue

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait ChatFlow {
  protected val killSwitchFlow: Flow[Message, Message, UniqueKillSwitch]

  protected def createFlow(implicit materializer: Materializer, ec: ExecutionContext) = {
    var (hubSink: Sink[Message, NotUsed], hubSource: Source[Message, NotUsed]) = MergeHub.source[Message](16).toMat(BroadcastHub.sink(256))(Keep.both).run()
    var killSwitchFlow: Flow[Message, Message, UniqueKillSwitch] = {
      Flow.fromSinkAndSource(hubSink, hubSource).joinMat((KillSwitches.singleBidi[Message, Message]))(Keep.right).backpressureTimeout(3 second)
    }
    killSwitchFlow
  }

  protected def chatFlow(source: Flow[String, Message, _], sink: Flow[Message, String, _])(implicit materializer: Materializer, ec: ExecutionContext) = {
    var flow = source.viaMat(killSwitchFlow)(Keep.right).viaMat(sink)(Keep.right)
    Future.successful(flow)
  }
}
