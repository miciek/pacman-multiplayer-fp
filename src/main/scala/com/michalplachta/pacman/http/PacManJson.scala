package com.michalplachta.pacman.http

import com.michalplachta.pacman.game.data.PacMan

protected trait PacManJson extends PositionJson with DirectionJson {
  implicit val pacManFormat = jsonFormat2(PacMan)
}
