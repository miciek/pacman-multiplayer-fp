package com.michalplachta.pacman.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.michalplachta.pacman.game.data.Grid
import spray.json._

protected trait GridJsonSupport extends SprayJsonSupport with DefaultJsonProtocol with PositionJsonSupport {
  implicit val gridJsonFormat = jsonFormat3(Grid)
}
