package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data.Grid

final case class ServerState(grids: Set[Grid], games: Set[ServerGame], nextGameId: Int)

object ServerState {
  val clean = ServerState(Set.empty, Set.empty, nextGameId = 1)
}
