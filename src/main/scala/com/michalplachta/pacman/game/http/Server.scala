package com.michalplachta.pacman.game.http

import akka.http.scaladsl.server.{HttpApp, Route}
import com.michalplachta.pacman.game.data.{Grid, Position}

object Server extends HttpApp with GridJsonSupport {
  val route: Route =
    pathPrefix("grid") {
      path("simpleSmall") {
        complete {
          val emptyCells: Set[Position] = (for {
            x <- 1 to 2
            y <- 1 to 2
          } yield Position(x, y)).toSet
          Grid(width = 3, height = 3, emptyCells)
        }
      }
    }

  protected def routes: Route = route
}

