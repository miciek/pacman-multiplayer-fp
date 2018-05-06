package com.michalplachta.pacman

import akka.http.scaladsl.server.Route
import com.michalplachta.pacman.game.{GameEngine, GridRepository}
import com.michalplachta.pacman.game.data.GameState
import akka.http.scaladsl.server.RouteConcatenation._
import com.michalplachta.pacman.http.HttpRoutes.{
  createGameRoute,
  getGameRoute,
  setDirectionRoute
}
import com.michalplachta.pacman.state.MultipleGamesState
import monix.execution.Scheduler
import monix.execution.atomic.Atomic

import scala.concurrent.duration._

class StatefulHttpRoute(tickScheduler: Scheduler,
                        tickDuration: FiniteDuration) {
  private val state = Atomic(Map.empty[Int, GameState])

  tickScheduler.scheduleWithFixedDelay(0.seconds, tickDuration) {
    state.transform(MultipleGamesState.tickAllGames(GameEngine.movePacMan))
  }

  def createGame: String => Either[String, GameState] =
    GridRepository.gridByName.andThen(GameEngine.start)

  def addGame(game: GameState): Int =
    state.transformAndExtract(MultipleGamesState.addGame(game))

  def getGame(gameId: Int): Option[GameState] =
    MultipleGamesState.getGame(gameId)(state.get)

  def updateGame(gameId: Int, gameState: GameState): Unit =
    state.transform(MultipleGamesState.updateGame(gameId, gameState))

  val route: Route =
    createGameRoute(createGame, addGame) ~
      getGameRoute[GameState](getGame, _.pacMan) ~
      setDirectionRoute(getGame, updateGame, GameEngine.changePacMansDirection)
}
