package com.michalplachta.pacman.game.data

sealed trait Direction

case object West extends Direction
case object East extends Direction
case object North extends Direction
case object South extends Direction
