package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data.GameState

final case class ServerGame(id: Int, gameState: GameState)
