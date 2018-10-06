package com.michalplachta.pacman

import akka.http.scaladsl.server.Route
import cats.effect.IO
import com.michalplachta.pacman.game.data.GameState
import com.michalplachta.pacman.game.{GameEngine, GridRepository}
import com.michalplachta.pacman.http.CollectiblesRequests
import com.michalplachta.pacman.http.HttpRoutes._
import monix.execution.Scheduler

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._

class StatefulHttpRoute(collectiblesRequests: CollectiblesRequests,
                        tickScheduler: Scheduler,
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

  def createCollectibles(id: Int, gameState: GameState, context: Map[String, String]): IO[Unit] = {
    collectiblesRequests.create(id, gameState.grid.usableCells, context)
  }

  val route: Route = {
    //createGameRoute(GridRepository.gridByName.andThen(GameEngine.start), addGame) ~
    createGameWithCollectiblesRoute(GridRepository.gridByName.andThen(GameEngine.start), addGame, createCollectibles) ~
    getGameRoute[GameState](state.get, _.pacMan) ~
    setDirectionRoute(state.get, state.update, GameEngine.changePacMansDirection) ~
    getGridRoute(GridRepository.gridByName)
  }
}
