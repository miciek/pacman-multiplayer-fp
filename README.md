# Pac-Man Multiplayer using Akka Streams

Companion project for Akka Streams/TDD workshop.

* Test Driven Development (baby steps, starting with the game logic and moving towards HTTP).
* Purely functional approach (separated data and behaviors).
* Modeling using immutable structures.
* Making impossible states impossible.

## Requirements

### Grid setup
  * There is a finite grid of cells.
  * Cells are either empty or blocked (a wall).
  * One of the empty cells can be occupied by Pac-Man.

### Movement
  * Pac-Man has a direction.
  * Pac-Man moves into the next cell in the desired direction.
  * Pac-Man cannot go through walls.
  * Pac-Man stops if the next cell in the desired direction is a wall.
  * Pac-Man wraps around the board.
  
### Player
  * Player can change Pac-Man's direction by rotating it.
  * Player cannot rotate Pac-Man into a wall.
  
### Dots
  * An empty cell can have dot inside.
  * Pac-Man eats a dot by going into a cell with dot inside.
  
### Gameplay
  * Score is a number of eaten dots.
