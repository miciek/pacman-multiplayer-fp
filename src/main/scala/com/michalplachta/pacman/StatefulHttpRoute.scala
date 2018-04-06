package com.michalplachta.pacman

import akka.http.scaladsl.server.Route
import com.michalplachta.pacman.game.GameEngine
//import akka.http.scaladsl.server.RouteConcatenation._
import com.michalplachta.pacman.http.HttpRoutes.{setDirectionRoute}
import com.michalplachta.pacman.state.MultipleGamesAtomicState
import monix.execution.Scheduler

import scala.concurrent.duration._

class StatefulHttpRoute(tickScheduler: Scheduler,
                        tickDuration: FiniteDuration) {
  private val atomicState = new MultipleGamesAtomicState

  tickScheduler.scheduleWithFixedDelay(0.seconds, tickDuration) {
    atomicState.tickAllGames(GameEngine.movePacMan)
  }

  val route: Route =
    setDirectionRoute(atomicState.getGame,
                      atomicState.setGame,
                      atomicState.setDirection)
}
