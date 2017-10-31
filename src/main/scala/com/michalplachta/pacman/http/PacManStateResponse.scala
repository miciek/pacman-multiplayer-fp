package com.michalplachta.pacman.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.michalplachta.pacman.game.data.PacMan
import spray.json.DefaultJsonProtocol

final case class PacManStateResponse(pacMan: PacMan)

object PacManStateResponse extends SprayJsonSupport with DefaultJsonProtocol with PacManJson {
  implicit val pacManStateResponseFormat = jsonFormat1(PacManStateResponse.apply)
}
