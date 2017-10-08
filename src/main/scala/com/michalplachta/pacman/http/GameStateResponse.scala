package com.michalplachta.pacman.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.michalplachta.pacman.game.data.PacMan
import spray.json.DefaultJsonProtocol

protected final case class GameStateResponse(step: Int, pacMan: PacMan)

protected object GameStateResponse extends SprayJsonSupport with DefaultJsonProtocol with PacManJson {
  implicit val gameStateResponseFormat = jsonFormat2(GameStateResponse.apply)
}
