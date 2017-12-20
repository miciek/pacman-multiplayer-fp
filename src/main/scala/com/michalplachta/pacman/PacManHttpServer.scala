package com.michalplachta.pacman

import java.time.Clock

import com.michalplachta.pacman.game.GameEngine
import com.michalplachta.pacman.game.data.{GameState, Grid}
import com.michalplachta.pacman.http.HttpHandler
import com.michalplachta.pacman.server.{Server, ServerState}
import monocle.macros.syntax.lens._

import scala.concurrent.duration._

class PacManHttpServer(clock: Clock, tickDuration: Duration) {
  val httpHandler = new HttpHandler[ServerState[GameState], GameState](
    ServerState.clean(clock.instant()),
    GameEngine.start(_, Grid.fromName),
    Server.addNewGame,
    Server.getGameFromState,
    Server.updateGameInState,
    _.pacMan,
    direction => gameState => gameState.lens(_.pacMan.nextDirection).set(Some(direction)),
    serverState => Server.tick(clock.instant(), tickDuration, GameEngine.movePacMan).runS(serverState).value
  )
}
