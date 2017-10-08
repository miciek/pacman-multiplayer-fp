package com.michalplachta.pacman.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

protected final case class StartGameRequest(gridName: String)

protected object StartGameRequest extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val startGameRequestFormat = jsonFormat1(StartGameRequest.apply)
}
