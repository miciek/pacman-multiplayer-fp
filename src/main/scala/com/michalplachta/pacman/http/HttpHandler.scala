package com.michalplachta.pacman.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive1, HttpApp, Route}
import com.michalplachta.pacman.game.data.{Grid, Position}
import com.michalplachta.pacman.server.{Server, ServerGame, ServerState}

import scala.util.Try

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
      parameterMap { params =>
        val stepStr = params.getOrElse("step", "0")
        gameFromState(gameId, stepStr) { game =>
          get {
            complete(PacManStateResponse(game.currentStep, game.pacMan))
          } ~
          put {
            entity(as[NewDirectionRequest]) { request =>
              complete(StatusCodes.OK)
            }
          }
        } ~
        complete((StatusCodes.NotFound, s"Game with the id $gameId and step $stepStr couldn't be found"))
      }
    }

  protected def routes: Route = route

  private def gameFromState(gameId: Int, stepStr: String): Directive1[ServerGame] = {
    val maybeGame = for {
      step <- Try(stepStr.toInt).toOption
      game <- serverState.games.find(_.id == gameId)
      if game.currentStep == step
    } yield game
    maybeGame.map(provide).getOrElse(reject)
  }
}

