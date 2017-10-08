package com.michalplachta.pacman.game.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.michalplachta.pacman.game.data.Grid
import spray.json._

trait GridJsonSupport extends SprayJsonSupport with DefaultJsonProtocol with PositionJsonSupport {
  implicit val gridJsonFormat = jsonFormat3(Grid)
}
