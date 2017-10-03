# Pac-Man Multiplayer using Akka Streams

Companion project for Akka Streams/TDD workshop.

## Requirements

### Grid
  * There is a finite grid of cells.
  * Cells are either empty or blocked (a wall).
  * One of the empty cells can be occupied by Pac-Man.

### Movement
  * Pac-Man has a direction.
  * Pac-Man moves into the next cell in the desired direction on each tick.
  * Pac-Man cannot go through walls.
  * Pac-Man stops if the next cell in the desired direction is a wall.
  
### Dots
  * An empty cell can have dot inside.
  * Pac-Man eats a dot by going into a cell with dot inside.
  
### Player
  * Player can change Pac-Man's direction by rotating it.
  * Player cannot rotate Pac-Man into a wall.
  
### Gameplay
  * Score is a number of eaten dots.
