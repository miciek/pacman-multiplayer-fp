package com.michalplachta.snake

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl._

class PacManMulti(implicit system: ActorSystem) {
  val flow: Flow[Message, Message, _] = {
    ???
  }
}
