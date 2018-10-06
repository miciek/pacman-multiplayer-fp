package com.michalplachta.pacman.http

import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import com.michalplachta.pacman.game.data.{Direction, Grid, PacMan}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import DirectionAsJson._
import cats.effect.IO

object HttpRoutes extends Directives {
  def createGameRoute[G](createGame: String => Either[String, G], addNewGame: G => Int): Route =
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

  def getGameRoute[G](getGame: Int => Option[G], getPacMan: G => PacMan): Route =
    path("games" / IntNumber) { gameId =>
      get {
        val maybeGame = getGame(gameId)
        maybeGame match {
          case Some(game) => complete(PacManStateResponse(getPacMan(game)))
          case _ =>
            complete((StatusCodes.NotFound, s"Pac-Man state for the game with id $gameId couldn't be found"))
        }
      }
    }

  def setDirectionRoute[G](getGame: Int => Option[G],
                           setGame: (Int, G) => Unit,
                           setDirection: Direction => G => G): Route =
    path("games" / IntNumber / "direction") { gameId =>
      put {
        entity(as[NewDirectionRequest]) { request =>
          val maybeGame = getGame(gameId)
          val maybeUpdatedGame =
            maybeGame.map(setDirection(request.newDirection))
          maybeUpdatedGame match {
            case Some(updatedGame) =>
              setGame(gameId, updatedGame)
              complete(StatusCodes.OK)
            case _ =>
              complete(StatusCodes.NotFound)
          }
        }
      }
    }

  def getGridRoute(getGrid: String => Grid): Route = {
    path("grids" / Segment) { gridName =>
      get {
        complete(GridResponse(getGrid(gridName)))
      }
    }
  }

  def createGameWithCollectiblesRoute[G](createGame: String => Either[String, G],
                                         addNewGame: G => Int,
                                         createCollectibles: (Int, G, Map[String, String]) => IO[Unit]): Route = {
    def getContext(request: HttpRequest): Map[String, String] = {
      request.headers.filter(_.name.startsWith("l5d")).map(h => (h.name, h.value)).toMap
      Map(
        "l5d-dst-residual" -> "/1.1/POST/pacman.exul.net/backend/games",
        "l5d-dst-client"   -> "/#/io.l5d.k8s/pacman/http/backend-test",
        "l5d-ctx-dtab"     -> "/svc=>/k8s/pacman/http/backend-test",
        "l5d-reqid"        -> "f2fd78005d756b0e",
        "l5d-ctx-trace"    -> "vYKCjy8tCNLy/XgAXXVrDvL9eABddWsOAAAAAAAAAAA=",
        "l5d-dst-service"  -> "/svc/1.1/POST/pacman.exul.net/backend/games"
      )

    }

    path("games") {
      post {
        extractRequest { request =>
          entity(as[StartGameRequest]) { startGame =>
            val startedGame = createGame(startGame.gridName)
            startedGame match {
              case Right(game) =>
                val gameId = addNewGame(game)
                onSuccess(createCollectibles(gameId, game, getContext(request)).unsafeToFuture) {
                  complete(StartGameResponse(gameId))
                }
              case Left(errorMessage) =>
                complete((StatusCodes.NotFound, errorMessage))
            }
          }
        }
      }
    }
  }
}
