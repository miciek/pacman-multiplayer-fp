package com.michalplachta.pacman.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.michalplachta.pacman.game.data.Grid
import spray.json.DefaultJsonProtocol

protected trait GridJson extends SprayJsonSupport with DefaultJsonProtocol with PositionJson {
  implicit val gridJsonFormat = jsonFormat3(Grid)
}
