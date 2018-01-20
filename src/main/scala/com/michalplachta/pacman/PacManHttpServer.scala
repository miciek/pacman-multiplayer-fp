package com.michalplachta.pacman

import java.time.Clock
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.server.Route
import com.michalplachta.pacman.game.GameEngine
import com.michalplachta.pacman.game.data.{Direction, GameState, Grid}
import com.michalplachta.pacman.server.{Server, ServerState}
import monocle.macros.syntax.lens._
import akka.http.scaladsl.server.RouteConcatenation._
import com.michalplachta.pacman.http.HttpHandler.{handleCreateGame, handleGetGame, handleGetGrid, handleSetDirection}
import monix.execution.Scheduler
import monix.execution.atomic.Atomic

import scala.concurrent.duration._

class PacManHttpServer(clock: Clock, tickDuration: Duration) {
  val atomicState = Atomic(ServerState.clean[GameState](clock.instant()))

  def addNewGame(game: GameState): Int = {
    atomicState.transformAndExtract { state =>
      Server.addNewGame(game).run(state).value.swap
    }
  }

  def getGame(gameId: Int): Option[GameState] = {
    atomicState.get.games.get(gameId)
  }

  def setGame(gameId: Int, game: GameState): Unit = {
    atomicState.transform { state =>
      Server.updateGameInState(gameId, game).runS(state).value
    }
  }

  def setDirection(direction: Direction)(gameState: GameState) =
    gameState.lens(_.nextPacManDirection).set(Some(direction))

  val scheduler = Scheduler.singleThread(name = "tick-games-thread")
  scheduler.scheduleWithFixedDelay(
    0, tickDuration.toMillis, TimeUnit.MILLISECONDS,
    () => atomicState.transform { state =>
        Server.tick(clock.instant(), tickDuration, GameEngine.movePacMan).runS(state).value
    }
  )

  val route: Route =
    handleGetGrid ~
    handleCreateGame(GameEngine.start(_, Grid.fromName), addNewGame) ~
    handleGetGame[GameState](getGame, _.pacMan) ~
    handleSetDirection(getGame, setGame, setDirection)
}
