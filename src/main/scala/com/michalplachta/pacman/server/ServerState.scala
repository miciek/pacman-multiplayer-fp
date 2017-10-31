package com.michalplachta.pacman.server

import java.time.Instant

final case class ServerState[S](games: Map[Int, S], lastTicked: Instant)

object ServerState {
  def clean[S](startedTime: Instant) = ServerState[S](Map.empty, startedTime)
}
