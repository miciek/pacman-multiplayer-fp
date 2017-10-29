package com.michalplachta.pacman.server

import java.time.{Clock, Instant}

import com.michalplachta.pacman.game.data.GameState

final case class ServerState(games: Map[Int, GameState], lastTicked: Instant)

object ServerState {
  val clean = ServerState(Map.empty, Clock.systemDefaultZone().instant())
}
