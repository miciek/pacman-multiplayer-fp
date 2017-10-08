package com.michalplachta.pacman.http

import com.michalplachta.pacman.game.data.Position
import spray.json.DefaultJsonProtocol

protected trait PositionJson extends DefaultJsonProtocol {
  implicit val positionFormat = jsonFormat2(Position)
}
