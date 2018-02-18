# Workshop: TDDing Functional Web Apps
Get some theoretical and practical overview of the TDD approach & Functional Programming by creating a multiplayer Pac-Man game server.

* First steps in Scala and Scalatest.
* Test Driven Development (baby steps, starting with the game logic and moving towards HTTP).
* Purely functional approach (separated data and behaviors, no exceptions, ADTs, `Option`s).
* Modeling using immutable structures.
* Making impossible states impossible.
* Using optics to deal with immutable changing data.
* Using function composition.
* Using refined types.
* Using Akka HTTP + Circe.
* Using Monix Atomic to handle state.
* Separate concerns by using functions as input parameters.

## Requirements
- Familiarity with basic Scala syntax (e.g. first 2 weeks of [Functional Programming Principles in Scala](https://www.coursera.org/learn/progfun1) or [Scala Tutorial](https://www.scala-exercises.org/scala_tutorial/terms_and_types))
- Please bring your laptop with Scala enabled environment. If you have problems setting it up, please [use contact page](http://michalplachta.com/contact/)

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
  * An empty cell can have a dot inside.
  * Pac-Man eats a dot by going into a cell with dot inside.
  
### Gameplay
  * Score is a number of eaten dots.
  
## Blog posts about this project
- [Building functional & testable HTTP APIs](http://michalplachta.com/2018/02/19/building-functional-testable-http-apis/)
