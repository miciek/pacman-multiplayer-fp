package com.michalplachta.pacman.http

import spray.json.DefaultJsonProtocol

protected final case class StartGameRequest(gridName: String)

protected object StartGameRequest extends DefaultJsonProtocol {
  implicit val startGameRequestFormat = jsonFormat1(StartGameRequest.apply)
}
