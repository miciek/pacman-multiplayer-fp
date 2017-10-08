package com.michalplachta.pacman.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.michalplachta.pacman.game.data.{Grid, Position}
import spray.json._

protected trait PositionJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val positionJsonSupport = jsonFormat2(Position)
}
