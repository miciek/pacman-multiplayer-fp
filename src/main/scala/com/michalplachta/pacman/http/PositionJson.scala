package com.michalplachta.pacman.http

import com.michalplachta.pacman.game.data.Position
import spray.json.DefaultJsonProtocol

protected trait PositionJson extends DefaultJsonProtocol {
  implicit val positionJsonSupport = jsonFormat2(Position)
}
