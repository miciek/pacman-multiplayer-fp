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
import monix.execution.Scheduler

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._

class StatefulHttpRoute(tickScheduler: Scheduler,
                        tickDuration: FiniteDuration) {
  private val state = TrieMap.empty[Int, GameState]

  tickScheduler.scheduleWithFixedDelay(0.seconds, tickDuration) {
    state.foreach { case (k, v) => state.update(k, GameEngine.movePacMan(v)) }
  }

  def addGame(game: GameState): Int = {
    val gameId = state.size
    state.put(gameId, game)
    gameId
  }

  val route: Route =
    createGameRoute(GridRepository.gridByName.andThen(GameEngine.start),
                    addGame) ~
      getGameRoute[GameState](state.get, _.pacMan) ~
      setDirectionRoute(state.get,
                        state.update,
                        GameEngine.changePacMansDirection)
}
