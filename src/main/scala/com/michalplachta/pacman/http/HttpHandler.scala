package com.michalplachta.pacman.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive1, HttpApp, Route}
import com.michalplachta.pacman.game.data.{Grid, Position}
import com.michalplachta.pacman.server.{Server, ServerGame, ServerState}

class HttpHandler(initialServerState: ServerState) extends HttpApp with GridJson {
  private var serverState = initialServerState

  val route: Route =
    path("grids" / "simpleSmall") {
      complete(Grid.simpleSmall)
    } ~
    path("games") {
      post {
        entity(as[StartGameRequest]) { request =>
          if (request.gridName == "simpleSmall") {
            val (newServerState, gameId) = Server.startNewGame(serverState)
            serverState = newServerState
            complete(StartGameResponse(gameId))
          } else {
            complete((StatusCodes.NotFound, s"Grid with the name '${request.gridName}' couldn't be found"))
          }
        }
      }
    } ~
    path("games" / IntNumber) { gameId =>
      gameFromState(gameId) { game =>
        get {
          complete(PacManStateResponse(game.currentStep, game.gameState.pacMan))
        } ~
        put {
          entity(as[NewDirectionRequest]) { _ =>
            complete(StatusCodes.OK)
          }
        }
      } ~
      complete((StatusCodes.NotFound, s"Game with the id $gameId couldn't be found"))
    }

  protected def routes: Route = route

  private def gameFromState(gameId: Int): Directive1[ServerGame] = {
    serverState.games
      .find(_.id == gameId)
      .map(provide)
      .getOrElse(reject)
  }
}

