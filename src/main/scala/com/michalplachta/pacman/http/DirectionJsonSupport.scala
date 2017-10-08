package com.michalplachta.pacman.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.michalplachta.pacman.game.data._
import spray.json._

protected trait DirectionJsonSupport extends SprayJsonSupport {
  implicit val directionFormat = new JsonFormat[Direction] {
    def read(json: JsValue): Direction = json match {
      case JsString("east") => East
      case JsString("west") => West
      case JsString("north") => North
      case JsString("south") => South
      case _ => deserializationError(s"Direction needs to be a JsString of 'east', 'west', 'north' or 'south'")
    }

    def write(obj: Direction): JsValue = obj match {
      case East => JsString("east")
      case West => JsString("west")
      case North => JsString("north")
      case South => JsString("south")
    }
  }
}
