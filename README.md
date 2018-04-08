# Workshop: TDDing Functional Web Apps
Get some theoretical and practical overview of the TDD approach & Functional Programming by creating a multiplayer Pac-Man game server.

* First steps in Scala and Scalatest.
* Letting the tools help you (linting, `wartremover`, `scalafmt`).
* Test Driven Development (baby steps, starting with the game logic and moving towards HTTP).
* Purely functional approach (separated data and behaviors, no exceptions, ADTs, `Option`s).
* Modeling using immutable structures.
* Separating the concerns by using functions as input parameters.
* Using Monix Atomic to handle state.
* Using optics to deal with immutable changing data.
* Making impossible states impossible (design, refined types).
* Using function composition to connect all the dots.

### Requirements
- Familiarity with basic Scala syntax (e.g. first 2 weeks of [Functional Programming Principles in Scala](https://www.coursera.org/learn/progfun1) or [Scala Tutorial](https://www.scala-exercises.org/scala_tutorial/terms_and_types))
- Please bring your laptop with Scala enabled environment and IDE. If you have problems setting them up, please [use contact page](http://michalplachta.com/contact/)

### Workshop plan

#### Part I: Immutability (warm-up)
- **theory**
  - using immutable structures
  - bags of functions
- **practice** - `GameEngine`
  - review `GameState` and how `GameEngine` contains functions that get and return `GameState`
  - *TODO*: make `"(not) start the game with specified grid"` tests green

#### Part II: Introduction to stateless tests & TDD using Scalatest
- **theory**
  - difference between tests and TDD tests
  - using traits to easily isolate test environments
- **practice** - `GameEngineTest`
  - review already implemented tests
  - review implementation: how `movePacMan` evolved into `"not move Pac-Man into a wall"` test
  - *TODO*: implement `"wrap Pac-Man around the grid (horizontally/vertically)"` failing tests and make them green
  - *TODO (optional)*: implement brand new `"move Pac-Man in the desired direction after wall ends"` failing test and make it green
  - all `GameEngineTest` tests should be green
  
#### Part III: Separating the concerns using functions
- **theory**
  - how to properly unit test the HTTP layer?
  - review `entangled.StatefulHttpRoutes` - *hard to test* means *entangled concerns*
  - using functions as parameters
  - abstracting over state using type parameters
- **practice** - `HttpRoutesTest`
  - live coding `"(not) allow creating a new game in chosen grid configuration"` test and implementation (using `GameState` first)
  - *TODO*: implement `"(not) allow getting Pac-Man's state in an (unknown) existing game"` failing tests and make them green
  - *TODO (optional)*: think how would you approach using newly created `HttpRoutes` functions in production server
- **reading**: 
  - blog post: [Building functional & testable HTTP APIs](http://michalplachta.com/2018/02/19/building-functional-testable-http-apis/)
  
#### Part IV: Handling state
- **theory**
  - separating state handling from logic (even when state is accessed concurrently)
  - most of our app is pure & stateless (even HTTP) - now we are entering impure land
  - Monix `Atomic` as a bridge towards more functional solutions
- **practice** - `MultipleGamesAtomicStateTest`
  - live coding `"allow adding a new game"` test and implementation
  - *TODO*: implement `"allow updating an existing game"` failing tests and make them green
  - *TODO*: implement `"update all games on tick"` failing tests and make them green
  - *TODO (optional)*: does `MultipleGamesAtomicState` need to know about `GameState`?
  
#### Part V: Connecting all dots
- **theory**
  - integration tests without running a server?
- **practice**
  - *TODO*: make `StatefulHttpRouteTest` green

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
