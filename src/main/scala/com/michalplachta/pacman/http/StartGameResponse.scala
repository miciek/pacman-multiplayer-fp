package com.michalplachta.pacman.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

protected final case class StartGameResponse(gameId: Int)

protected object StartGameResponse extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val startGameResponseFormat = jsonFormat1(StartGameResponse.apply)
}
