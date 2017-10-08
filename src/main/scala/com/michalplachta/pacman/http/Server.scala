package com.michalplachta.pacman.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{HttpApp, Route}
import com.michalplachta.pacman.game.data.{East, Grid, PacMan, Position}

object Server extends HttpApp with GridJson {
  val route: Route =
    path("grids" / "simpleSmall") {
      complete {
        val emptyCells: Set[Position] = (for {
          x <- 1 to 2
          y <- 1 to 2
        } yield Position(x, y)).toSet
        Grid(width = 3, height = 3, emptyCells)
      }
    } ~
    path("games") {
      post {
        entity(as[StartGameRequest]) { request =>
          if (request.gridName == "simpleSmall") {
            complete(StartGameResponse(gameId = 1))
          } else {
            complete((StatusCodes.NotFound, s"Grid with the name '${request.gridName}' couldn't be found"))
          }
        }
      }
    } ~
    path("games" / IntNumber) { gameId =>
      get {
        if(gameId == 1) {
          complete(GameStateResponse(clock = 0, PacMan(Position(1, 1), direction = East)))
        } else {
          complete((StatusCodes.NotFound, s"Game with the id $gameId couldn't be found"))
        }
      }
    }

  protected def routes: Route = route
}

