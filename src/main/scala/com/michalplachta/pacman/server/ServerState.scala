package com.michalplachta.pacman.server

import java.time.{Clock, Instant}

final case class ServerState[S](games: Map[Int, S], lastTicked: Instant)

object ServerState {
  def clean[S] = ServerState[S](Map.empty, Clock.systemDefaultZone().instant())
}
