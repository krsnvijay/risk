# Architectural Design

We follow a model similar to the Model View Controller (MVC) pattern for structuring our project. This is how the directory structure looks like:
![Directory Structure](https://i.imgur.com/jjKAIFn.png)
 
![Architectural Diagram](https://i.imgur.com/4sDe6dU.png)

## The Models package 
This package holds the definition for the various entities in the game: `Continent, Country, GameMap, Card` etc. It also has a player package with the various strategies and the `Player` class. 

## The Views package
This package houses the `Runner` class which runs the entire project, including launching the GUI to display information to the user. It also has separate classes for the `PhaseView`, the `WorldDominationView`, and the `CardExchangeView`.

## The Controllers package
This package has the `GameController` which has methods to process various commands with the correct strategy and is in charge of running the Game Loop. We also have controllers for the Editor, Startup, and Setup phases, `EditorController`, `StartUpController`, and `SetupController` respectively. The Attack Phase is handled using a special `BattleController` to handle its intricacies.

## The Utils package
This package has all sorts of utility methods to help our processing. The map is processed with the help of the `MapParser`, `MapAdaptor`, and `MapValidator` utilities. We also have a `CLI` class for our various command line utilities.

## Some implementation details
Our bifurcation allows us to have different logical components in their respective directories. The unit tests are in the test folder separated using the same directory structure as the main Java source.
Our main method resides in the `Runner` class which allows the players to type in commands on a CLI. Using the EditMap and MapParser utilities, we handle .map files parsing and editing. The GameController takes over when the player is playing the game. Throughout the project, the `CLI` utility class maintains the context using an enum to validate which commands are allowed in the current phase.

This enum is maintained in the `Context` class (models package). Snippets of the code are shown below.

```java
// Handle the various phases for validation
public enum Context {
  MAIN_MENU(EDIT_MAP, GAME_PLAYER, LOAD_MAP),
  MAP_EDITOR(SHOW_MAP, EDIT_CONTINENT, ...),
  GAME_SETUP(...),
  GAME_STARTUP(...),
  GAME_REINFORCE(...),
  GAME_ATTACK(...);

  Context(Command... validCommands) {
    ...
  }

  boolean validate(String riskCommand) {
    ...
  }

  boolean runCommand(GameMap gameMap, String riskCommand) {
    ...
  }

  Command[] getValidCommands() {
    ...
  }
}
```

The `GameMap` is the singleton object holding the current state of the game. A reference to this object will be used while processing most gameplay commands. The validations and management of the game phases is all done within the `GameController`.

We implemented the Observer pattern to handle view updation. Our `GameMap`, `Player`, and `WorldDomination` classes are the subjects that extend the Observable Java class.
The observer interface is implemented in the `CardExchangeView`, `PhaseView`, and `WDView` in the GUI.
