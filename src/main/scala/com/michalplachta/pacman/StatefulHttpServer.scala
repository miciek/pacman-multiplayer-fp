package com.michalplachta.pacman

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.server.Route
import com.michalplachta.pacman.game.GameEngine
import com.michalplachta.pacman.game.data.{Direction, GameState, Grid}
import monocle.macros.syntax.lens._
import akka.http.scaladsl.server.RouteConcatenation._
import com.michalplachta.pacman.http.HttpHandlers.{handleCreateGame, handleGetGame, handleGetGrid, handleSetDirection}
import monix.execution.Scheduler
import monix.execution.atomic.Atomic

import scala.concurrent.duration._

class StatefulHttpServer(tickDuration: Duration) {
  val atomicState = Atomic(Map.empty[Int, GameState])

  def addNewGame(game: GameState): Int = {
    atomicState.transformAndExtract { state =>
      val gameId = state.size
      val newState = state + (gameId -> game)
      (gameId, newState)
    }
  }

  def getGame(gameId: Int): Option[GameState] = {
    atomicState.get.get(gameId)
  }

  def setGame(gameId: Int, game: GameState): Unit = {
    atomicState.transform(_.updated(gameId, game))
  }

  def setDirection(direction: Direction)(gameState: GameState) =
    gameState.lens(_.nextPacManDirection).set(Some(direction))

  val scheduler = Scheduler.singleThread(name = "tick-games-thread")
  scheduler.scheduleWithFixedDelay(
    0, tickDuration.toMillis, TimeUnit.MILLISECONDS,
    () => atomicState.transform(_.mapValues(GameEngine.movePacMan))
  )

  val route: Route =
    handleGetGrid ~
    handleCreateGame(GameEngine.start(_, Grid.fromName), addNewGame) ~
    handleGetGame[GameState](getGame, _.pacMan) ~
    handleSetDirection(getGame, setGame, setDirection)
}
