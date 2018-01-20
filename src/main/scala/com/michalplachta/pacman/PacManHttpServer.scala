package com.michalplachta.pacman

import java.time.Clock

import akka.http.scaladsl.server.Route
import com.michalplachta.pacman.game.GameEngine
import com.michalplachta.pacman.game.data.{Direction, GameState, Grid}
import com.michalplachta.pacman.http.HttpHandler
import com.michalplachta.pacman.server.{Server, ServerState}
import monocle.macros.syntax.lens._
import akka.http.scaladsl.server.RouteConcatenation._

import scala.concurrent.duration._

class PacManHttpServer(clock: Clock, tickDuration: Duration) {
  val httpHandler = new HttpHandler[ServerState[GameState], GameState](ServerState.clean(clock.instant()))

  def tick(serverState: ServerState[GameState]) =
    Server.tick(clock.instant(), tickDuration, GameEngine.movePacMan).runS(serverState).value
  def setDirection(direction: Direction)(gameState: GameState) =
    gameState.lens(_.nextPacManDirection).set(Some(direction))

  val route: Route =
    httpHandler.handleGetGrid ~
    httpHandler.handleCreateGame(GameEngine.start(_, Grid.fromName), Server.addNewGame) ~
    httpHandler.handleGetGame(Server.getGameFromState, _.pacMan, tick) ~
    httpHandler.handleSetDirection(Server.getGameFromState, Server.updateGameInState, setDirection)
}
