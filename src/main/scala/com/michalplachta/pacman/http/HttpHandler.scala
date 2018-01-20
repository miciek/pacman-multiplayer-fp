package com.michalplachta.pacman.http

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import cats.data.State
import com.michalplachta.pacman.game.data.{Direction, Grid, PacMan}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.refined._
import DirectionAsJson._

class HttpHandler[S, G](initialState: S) extends Directives {
  private var state: S = initialState

  val handleGetGrid: Route =
    path("grids" / "simpleSmall") {
      complete(Grid.simpleSmall)
    }

  def handleCreateGame(createGame: String => Either[String, G], addNewGame: G => State[S, Int]): Route =
    path("games") {
      post {
        entity(as[StartGameRequest]) { request =>
          val startedGame = createGame(request.gridName)
          startedGame match {
            case Right(game) =>
              val (newState, gameId) = addNewGame(game).run(state).value
              state = newState
              complete(StartGameResponse(gameId))
            case Left(errorMessage) =>
              complete((StatusCodes.NotFound, errorMessage))
          }
        }
      }
    }

  def handleGetGame(getGameState: Int => State[S, Option[G]],
                    getPacMan: G => PacMan,
                    tick: S => S): Route =
    path("games" / IntNumber) { gameId =>
      get {
        val tickedState = tick(state)
        val (newState, maybeGame) = getGameState(gameId).run(tickedState).value
        state = newState
        maybeGame match {
          case Some(game) => complete(PacManStateResponse(getPacMan(game)))
          case _ => complete((StatusCodes.NotFound, s"Pac-Man state for the game with id $gameId couldn't be found"))
        }
      }
    }

  def handleSetDirection(getGameState: Int => State[S, Option[G]],
                         setGameState: (Int, G) => State[S, Unit],
                         setDirection: Direction => G => G): Route =
    path("games" / IntNumber / "direction") { gameId =>
      put {
        entity(as[NewDirectionRequest]) { request =>
          val updateGame: State[S, StatusCode] = for {
            maybeGame <- getGameState(gameId)
            maybeUpdatedGame = maybeGame.map(setDirection(request.newDirection))
            result <- maybeUpdatedGame match {
              case Some(updatedGame) => setGameState(gameId, updatedGame).map(_ => StatusCodes.OK)
              case _ => State.pure[S, StatusCode](StatusCodes.NotFound)
            }
          } yield result
          val (newState, result) = updateGame.run(state).value
          state = newState
          complete(result)
        }
      }
    }
}

