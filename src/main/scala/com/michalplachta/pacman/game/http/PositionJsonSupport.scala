package com.michalplachta.pacman.game.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.michalplachta.pacman.game.data.{Grid, Position}
import spray.json._

trait PositionJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val positionJsonSupport = jsonFormat2(Position)
}
