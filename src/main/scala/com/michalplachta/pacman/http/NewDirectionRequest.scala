package com.michalplachta.pacman.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.michalplachta.pacman.game.data.Direction
import spray.json.DefaultJsonProtocol

protected final case class NewDirectionRequest(newDirection: Direction)

protected object NewDirectionRequest extends SprayJsonSupport with DefaultJsonProtocol with DirectionJson {
  implicit val newDirectionRequestFormat = jsonFormat1(NewDirectionRequest.apply)
}
