package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data.GameState

// TODO: use concrete types instead of Ints
final case class ServerGame(id: Int, currentStep: Int, gameState: GameState)
