package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data.PacMan

final case class ServerGame(id: Int, currentStep: Int, pacMan: PacMan)
