package com.michalplachta.pacman.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.michalplachta.pacman.game.data.{Direction, Grid, PacMan}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.refined._
import DirectionAsJson._

object HttpHandlers extends Directives {
  val handleGetGrid: Route =
    path("grids" / "simpleSmall") {
      complete(Grid.simpleSmall)
    }

  def handleCreateGame[G](createGame: String => Either[String, G], addNewGame: G => Int): Route =
    path("games") {
      post {
        entity(as[StartGameRequest]) { request =>
          val startedGame = createGame(request.gridName)
          startedGame match {
            case Right(game) =>
              val gameId = addNewGame(game)
              complete(StartGameResponse(gameId))
            case Left(errorMessage) =>
              complete((StatusCodes.NotFound, errorMessage))
          }
        }
      }
    }

  def handleGetGame[G](getGameState: Int => Option[G], getPacMan: G => PacMan): Route =
    path("games" / IntNumber) { gameId =>
      get {
        val maybeGame = getGameState(gameId)
        maybeGame match {
          case Some(game) => complete(PacManStateResponse(getPacMan(game)))
          case _ => complete((StatusCodes.NotFound, s"Pac-Man state for the game with id $gameId couldn't be found"))
        }
      }
    }

  def handleSetDirection[G](getGameState: Int => Option[G],
                            setGameState: (Int, G) => Unit,
                            setDirection: Direction => G => G): Route =
    path("games" / IntNumber / "direction") { gameId =>
      put {
        entity(as[NewDirectionRequest]) { request =>
          val maybeGame = getGameState(gameId)
          val maybeUpdatedGame = maybeGame.map(setDirection(request.newDirection))
          maybeUpdatedGame match {
            case Some(updatedGame) =>
              setGameState(gameId, updatedGame)
              complete(StatusCodes.OK)
            case _ =>
              complete(StatusCodes.NotFound)
          }
        }
      }
    }
}

