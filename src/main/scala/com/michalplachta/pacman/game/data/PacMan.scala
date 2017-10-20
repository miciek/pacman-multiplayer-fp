package com.michalplachta.pacman.game.data

case class PacMan(position: Position, direction: Direction, nextDirection: Option[Direction] = None)
