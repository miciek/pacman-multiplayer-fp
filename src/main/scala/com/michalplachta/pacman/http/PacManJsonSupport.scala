package com.michalplachta.pacman.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.michalplachta.pacman.game.data.PacMan

protected trait PacManJsonSupport extends SprayJsonSupport with PositionJsonSupport with DirectionJsonSupport {
  implicit val pacManJsonSupport = jsonFormat2(PacMan)
}
