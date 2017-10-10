package com.michalplachta.pacman.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{HttpApp, Route}
import com.michalplachta.pacman.game.data.{East, Grid, PacMan, Position}
import com.michalplachta.pacman.server.{Server, ServerState}

class HttpHandler(initialServerState: ServerState) extends HttpApp with GridJson {
  private var serverState = initialServerState

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
            serverState = Server.startNewGame(serverState)
            complete(StartGameResponse(serverState.games.last.id))
          } else {
            complete((StatusCodes.NotFound, s"Grid with the name '${request.gridName}' couldn't be found"))
          }
        }
      }
    } ~
    path("games" / IntNumber) { gameId =>
      if(initialServerState.games.exists(_.id == gameId)) {
        get {
          complete(PacManStateResponse(step = 0, PacMan(Position(1, 1), direction = East)))
        } ~
        put {
          entity(as[NewDirectionRequest]) { request =>
            complete(StatusCodes.OK)
          }
        }
      } else {
        complete((StatusCodes.NotFound, s"Game with the id $gameId couldn't be found"))
      }
    }

  protected def routes: Route = route
}

