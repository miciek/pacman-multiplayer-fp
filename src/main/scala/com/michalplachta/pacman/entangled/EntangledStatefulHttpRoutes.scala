package com.michalplachta.pacman.entangled

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.michalplachta.pacman.game.GameEngine
import com.michalplachta.pacman.game.GridRepository.gridByName
import com.michalplachta.pacman.game.data.GameState
import com.michalplachta.pacman.http.{NewDirectionRequest, PacManStateResponse, StartGameRequest, StartGameResponse}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

/**
  * WARNING!
  *
  * This class demonstrates _WRONG_ approach to building HTTP APIs that
  * entangles concerns: state is entangled inside HTTP layer.
  *
  * See [[com.michalplachta.pacman.http.HttpRoutes]] to see better approach.
  */
class EntangledStatefulHttpRoutes extends Directives {
  private var state = Map.empty[Int, GameState]

  val routes: Route = {
    path("games") {
      post {
        entity(as[StartGameRequest]) { request =>
          val startedGame = GameEngine.start(gridByName(request.gridName))
          startedGame match {
            case Right(game) =>
              val gameId = state.size
              state = state + (gameId -> game)
              complete(StartGameResponse(gameId))
            case Left(errorMessage) =>
              complete((StatusCodes.NotFound, errorMessage))
          }
        }
      }
    } ~
    path("games" / IntNumber) { gameId =>
      get {
        val maybeGame = state.get(gameId)
        maybeGame match {
          case Some(game) => complete(PacManStateResponse(game.pacMan))
          case _ =>
            complete((StatusCodes.NotFound, s"Pac-Man state for the game with id $gameId couldn't be found"))
        }
      }
    } ~
    path("games" / IntNumber / "direction") { gameId =>
      put {
        entity(as[NewDirectionRequest]) { request =>
          val maybeGame        = state.get(gameId)
          val maybeUpdatedGame = maybeGame.map(game => game.copy(nextPacManDirection = Some(request.newDirection)))
          maybeUpdatedGame match {
            case Some(updatedGame) =>
              state = state + (gameId -> updatedGame)
              complete(StatusCodes.OK)
            case _ =>
              complete(StatusCodes.NotFound)
          }
        }
      }
    }
  }
}
