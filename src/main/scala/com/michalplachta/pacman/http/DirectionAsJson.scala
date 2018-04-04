package com.michalplachta.pacman.http

import com.michalplachta.pacman.game.data._
import io.circe._

object DirectionAsJson {
  implicit val encodeDirection: Encoder[Direction] = {
    case West  => Json.fromString("west")
    case East  => Json.fromString("east")
    case North => Json.fromString("north")
    case South => Json.fromString("south")
  }

  implicit val decodeDirection: Decoder[Direction] = Decoder.decodeString.emap {
    case "west"  => Right(West)
    case "east"  => Right(East)
    case "north" => Right(North)
    case "south" => Right(South)
    case other =>
      Left(
        s"direction of '$other' is not one of: 'west', 'east', 'north' or 'south'")
  }
}
