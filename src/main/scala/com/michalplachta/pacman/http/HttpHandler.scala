package com.michalplachta.pacman.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive1, HttpApp, Route}
import com.michalplachta.pacman.game.data.{Direction, Grid, PacMan}

class HttpHandler[S](initialState: S,
                     startNewGame: S => (S, Int),
                     getPacMan: (S, Int) => Option[PacMan],
                     setNewDirection: (S, Int, Direction) => S
                    ) extends HttpApp with GridJson {
  private var state: S = initialState

  val route: Route =
    path("grids" / "simpleSmall") {
      complete(Grid.simpleSmall)
    } ~
    path("games") {
      post {
        entity(as[StartGameRequest]) { request =>
          if (request.gridName == "simpleSmall") {
            val (newServerState, gameId) = startNewGame(state)
            state = newServerState
            complete(StartGameResponse(gameId))
          } else {
            complete((StatusCodes.NotFound, s"Grid with the name '${request.gridName}' couldn't be found"))
          }
        }
      }
    } ~
    path("games" / IntNumber) { gameId =>
      pacManFromState(gameId) { pacMan =>
        get {
          complete(PacManStateResponse(pacMan))
        }
      } ~
      complete((StatusCodes.NotFound, s"Pac-Man state for the game with id $gameId couldn't be found"))
    } ~
    path("games" / IntNumber / "direction") { gameId =>
      put {
        entity(as[NewDirectionRequest]) { request =>
          state = setNewDirection(state, gameId, request.newDirection)
          complete(StatusCodes.OK)
        }
      }
    }

  protected def routes: Route = route

  private def pacManFromState(gameId: Int): Directive1[PacMan] = {
    val maybePacMan = getPacMan(state, gameId)
    maybePacMan.map(provide).getOrElse(reject)
  }
}

