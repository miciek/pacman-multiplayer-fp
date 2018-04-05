# Workshop: TDDing Functional Web Apps
Get some theoretical and practical overview of the TDD approach & Functional Programming by creating a multiplayer Pac-Man game server.

* First steps in Scala and Scalatest.
* Test Driven Development (baby steps, starting with the game logic and moving towards HTTP).
* Purely functional approach (separated data and behaviors, no exceptions, ADTs, `Option`s).
* Modeling using immutable structures.
* Separate concerns by using functions as input parameters.
* Using Monix Atomic to handle state.
* Using optics to deal with immutable changing data.
* Making impossible states impossible (design, refined types).
* Using function composition to connect all the dots.

### Requirements
- Familiarity with basic Scala syntax (e.g. first 2 weeks of [Functional Programming Principles in Scala](https://www.coursera.org/learn/progfun1) or [Scala Tutorial](https://www.scala-exercises.org/scala_tutorial/terms_and_types))
- Please bring your laptop with Scala enabled environment. If you have problems setting it up, please [use contact page](http://michalplachta.com/contact/)

### Workshop plan

#### Part I: Immutability (warm-up)
- **theory**
  - using immutable structures
  - bags of functions
- **practice** - `GameEngine`
  - review `GameState` and how `GameEngine` contains functions that get and return `GameState`
  - *TODO*: implement "`(not) start the game with specified grid`" tests

#### Part II: Introduction to stateless tests & TDD using Scalatest
- **theory**
  - difference between tests and TDD tests
  - using traits to easily isolate test environments
- **practice** - `GameEngine`
  - review already implemented tests
  - review implementation: how `movePacMan` evolved into `"not move Pac-Man into a wall"` test
  - *TODO*: implement "`wrap Pac-Man around the grid`" tests
  - *TODO (optional)*: implement "`move Pac-Man in the desired direction after wall ends`" test
  
#### Part III: Separating the concerns using functions
- **theory**
  - how to properly unit test the HTTP layer?
  - review `entangled.StatefulHttpRoutes` - *hard to test* means *entangled concerns*
  - abstracting over state using type parameters
  - using functions as parameters
- **practice**
  - live coding `"(not) allow creating a new game in chosen grid configuration"` test and implementation
  - *TODO*: implement `"(not) allow getting Pac-Man's state in an existing game"`
- **reading**: 
  - blog post: [Building functional & testable HTTP APIs](http://michalplachta.com/2018/02/19/building-functional-testable-http-apis/)

## Game Features

### Grid setup
  * There is a finite grid of cells.
  * Cells are either usable or blocked (a wall).
  * One of the usable cells can be occupied by Pac-Man.

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
  * A usable cell can have a dot inside.
  * Initially all usable cells have dots.
  * Pac-Man eats a dot by going into a cell with dot inside.
  
### Gameplay
  * Score is a number of eaten dots.
