package com.michalplachta.pacman.http

import akka.http.scaladsl.model.StatusCodes
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
    } ~
    path("games") {
      post {
        entity(as[StartGameRequest]) { request =>
          if(request.gridName == "simpleSmall") {
            complete(StartGameResponse(gameId = 1))
          } else {
            complete((StatusCodes.NotFound, s"Grid with the name '${request.gridName}' couldn't be found"))
          }
        }
      }
    }

  protected def routes: Route = route
}

