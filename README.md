# Pac-Man multiplayer server - Scala, FP & Service Meshes
Project is a playground for playing with simple functional programming tools in Scala to build services that are easily deployable, scalable and monitored. It is used in some of [my](https://www.michalplachta.com) articles, workshops and talks which are listed at the bottom.

The application can be built and deployed to [Docker](https://www.docker.com/) and/or [Kubernetes](https://kubernetes.io) with [Istio](https://istio.io/docs/setup/kubernetes/quick-start.html) service mesh. Frontend application can be found in [Reactive Pac-Man repository](https://github.com/miciek/web-pacman-react-bacon).

### Building & deploying
The following assumes that you have `docker`, `minikube`, `kubectl` and `istioctl` commands available. Please refer to [Minikube section in this tutorial to set it up before moving on](https://istio.io/docs/setup/kubernetes/quick-start.html)

Run [build_and_deploy.sh](./build_and_deploy.sh) to build fat-jar, docker image and deploy to `minikube` with `istio` sidecars.

### Testing manually using cURL
```
> curl -H "Content-Type: application/json" -v http://localhost:8080/games -d '{ "gridName": "small" }'
{"gameId":1}
> curl http://localhost:8080/backend/games/1
{"pacMan":{"position":{"x":0,"y":0},"direction":"east"}}
> curl http://localhost:8080/backend/games/1
{"pacMan":{"position":{"x":1,"y":0},"direction":"east"}}
> curl http://localhost:8080/backend/games/1
{"pacMan":{"position":{"x":2,"y":0},"direction":"east"}}
> curl -XPUT -H "Content-Type: application/json" http://localhost:8080/games/1/direction -d '{ "newDirection": "south" }'
OK
> curl http://localhost:8080/backend/games/1
{"pacMan":{"position":{"x":2,"y":1},"direction":"south"}}
```

If deploying to `minikube`, please use `$GATEWAY_URL` instead of `localhost`. You can obtain this URL using `export GATEWAY_URL=$(minikube ip):$(kubectl get svc istio-ingress -n istio-system -o 'jsonpath={.spec.ports[0].nodePort}')`.

## Workshop: TDDing Functional Web Apps
Get some theoretical and practical overview of the TDD approach & Functional Programming by creating a multiplayer Pac-Man game server.

* First steps in Scala and Scalatest.
* Test Driven Development (baby steps, starting with the game logic and moving towards HTTP).
* Purely functional approach (separated data and behaviors, no exceptions, ADTs, `Option`s).
* Modeling using immutable structures.
* Separating the concerns by using functions as input parameters.
* Creating loosely coupled modules by using type parameters.
* Using function composition to connect all the dots.
* Using optics to deal with immutable changing data.

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
  
#### Part IV: Connecting all dots
- **theory**
  - integration tests without running a server?
- **practice**
  - *TODO*: make `StatefulHttpRouteTest` green

## Talk: Building testable APIs with functions and meshes
In this live coding talk you will learn about functional techniques that encourage separation of concerns. I will build a stateful HTTP API from highly isolated components that are easier to test than entangled spaghetti-like codebases. I will show you how tests can help us scaffold the architecture and how should we approach testing in the era of microservices. In the session I will use immutability, type parameters and function parameters as tools to implement a very practical example: Pac-Man game web server. I will code in Scala using Akka HTTP and deploy to Kubernetes with Istio service mesho.

Slides are available on [speakerdeck](https://speakerdeck.com/miciek/building-testable-apis-using-functions-and-meshes).
