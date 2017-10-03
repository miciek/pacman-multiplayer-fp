package com.michalplachta.pacman.game.data

sealed trait Direction {
  val x: Int
  val y: Int
}

case object West extends Direction {
  val x: Int = -1
  val y: Int = 0
}

case object East extends Direction {
  val x: Int = 1
  val y: Int = 0
}

case object North extends Direction {
  val x: Int = 0
  val y: Int = 1
}

case object South extends Direction {
  val x: Int = 0
  val y: Int = -1
}
