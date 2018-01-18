package com.michalplachta.pacman.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive1, HttpApp, Route}
import cats.data.State
import com.michalplachta.pacman.game.data.{Direction, Grid, PacMan}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import DirectionAsJson._

class HttpHandler[S, G](initialState: S,
                        startNewGame: String => Either[String, G],
                        addNewGame: G => State[S, Int],
                        getGameState: Int => State[S, Option[G]],
                        setGameState: (Int, G) => State[S, Unit],
                        getPacMan: G => PacMan,
                        setDirection: Direction => G => G,
                        tick: S => S
                       ) extends HttpApp {
  private var state: S = initialState

  val route: Route =
    path("grids" / "simpleSmall") {
      complete(Grid.simpleSmall)
    } ~
    path("games") {
      post {
        entity(as[StartGameRequest]) { request =>
          val startedGame = startNewGame(request.gridName)
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
    } ~
    path("games" / IntNumber) { gameId =>
      getGameFromState(gameId) { game =>
        get {
          complete(PacManStateResponse(getPacMan(game)))
        }
      } ~
      complete((StatusCodes.NotFound, s"Pac-Man state for the game with id $gameId couldn't be found"))
    } ~
    path("games" / IntNumber / "direction") { gameId =>
      getGameFromState(gameId) { game =>
        put {
          entity(as[NewDirectionRequest]) { request =>
            val updatedGame = setDirection(request.newDirection)(game)
            state = setGameState(gameId, updatedGame).runS(state).value
            complete(StatusCodes.OK)
          }
        }
      }
    }

  protected def routes: Route = route

  private def getGameFromState(gameId: Int): Directive1[G] = {
    val tickedState = tick(state)
    val (newState, maybeGame) = getGameState(gameId).run(tickedState).value
    state = newState
    maybeGame.map(provide).getOrElse(reject)
  }
}

