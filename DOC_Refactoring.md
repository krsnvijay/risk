# Refactors
This document contains information on the refactors we made.

# From Build 3
Our main focus of *build#3* is to polish our code and ensure everything stays optimised. The tournament mode is going to execute a lot of calls very fast and we want to keep everything in check.

## Potential Refactoring Targets
* Implementation of Strategy pattern changes how our Player class behaves.
* Making the code more understandable and efficient by caching chained calls to methods.
* Renaming classes and variables for understanding and consistency.
* Handling the number of method calls and information in memory.
* Implementation of the Adaptor pattern to read Conquest map files as well as the Dominance game files (as before).

## Refactoring Operations
These are the refactoring operations we performed.

### Implementation of the Strategy pattern
For the last two builds, we had one Player class that dealt with human players. With the implementation of the strategy pattern, our Player class was refactored into `PlayerHuman`. The methods from that class which were common to every strategy were moved into the `Player` class, ex. `getCountriesByOwnership()` or `calculateReinforcements(GameMap gameMap)`.

This new `Player` class holds an instance of `PlayerStrategy` hence holding an instance of a strategy through Composition. Our original code still passed the tests since we refactored instances of the 'old' `Player` into `player.getStrategy()` to get an instance of `PlayerStrategy` -- `PlayerHuman`.

```java
public class Player {
  private PlayerStrategy strategy = null;
  
  public Player(String name, String strategy) {
    switch (strategy) {
      case "random":
        this.setStrategy(new PlayerRandom(name));
        break;
      case "aggressive":
        this.setStrategy(new PlayerAggressive(name));
        break;
      case "benevolent":
        this.setStrategy(new PlayerBenevolent(name));
        break;
      case "cheater":
        this.setStrategy(new PlayerCheater(name));
        break;
      case "human":
        this.setStrategy(new PlayerHuman(name));
        break;
    }
  }
  
  public static int calculateReinforcements(GameMap gameMap) {
    String playerName = gameMap.getCurrentPlayer().strategy.getPlayerName();
    int ownedCountries = getCountriesByOwnership(playerName, gameMap).size();
    int allReinforcementArmies = getBonusArmiesIfPlayerOwnsContinents(playerName, gameMap);

    if (ownedCountries < 9) {
      allReinforcementArmies += 3;
    }
    allReinforcementArmies += ownedCountries / 3;
    return allReinforcementArmies;
  }
  
  // ...
  
  public PlayerStrategy getStrategy() {
    return strategy;
  }

  public void setStrategy(PlayerStrategy strategy) {
    this.strategy = strategy;
  }

  public void attack(GameMap gameMap, String command) {
    strategy.attack(gameMap, command);
  }

  public void reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace) {
    strategy.reinforce(gameMap, countryToPlace, armiesToPlace);
  }
  
  public void fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove) {
    strategy.fortify(gameMap, fromCountry, toCountry, armyToMove);
  }
}
```

The old `Player` class looks like this now:

```java
public class PlayerHuman extends Observable implements PlayerStrategy {

  private String playerName;
  private int numberOfArmies;
 
  public PlayerHuman(String playerName) {
    super();
    this.playerName = playerName;
  }
  
  public boolean reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace) {
    //...
  }

  public boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove) {
    //...
  }
  
  public boolean attack(GameMap gameMap, String command) {
    //...
  }
```

### Caching calls to long method chains


# From Build 2
Our main focus of *build#2* is to improve the code that processes and validates the user input from the cli.

In *build#1* each command is handled and validated inside their own separate loop. 
This made the code base difficult to understand and harder to test cli commands.
Now, the process is much more streamlined, With only one loop that handles the input.
First the input is matched with a list of possible commands in the current context,
If there is a match then the command is sent to an appropriate controller where it can be processed further.
Unlike the previous build we use regex to validate the input, this allows us to detect errors in the cli usage very early on in the pipeline.
 
## Potential Refactoring Targets
* Context validation of command
* Command syntax validation
* Command option validation
* Split behaviour of each phase into their own controller
* Move player actions like attack, fortify, reinforce into Player class
* Refactor list of possible commands to 'help' command
* Delegate console output to display() in Views, which also works with the GUI to show feedback
* Refactor GameMap class to a singleton that is observable 

## Refactoring Operations
These are the refactoring operations we performed.

### Command syntax validation
The commands allowed in the game are stored as enums where each type contains the regex pattern, the controller method ref that processes the command and its usage.
Refactoring is done by extracting methods, validation , usage from ```GameRunner, Runner``` of *build#1*.
Convenience methods like ```validate, getUsage, runOperation``` are added for ease of use.
```java
public enum Command {
  GAME_HELP("^help$", MainController::processGameHelpCommand, "help"),
  EXIT_GAME("^exitgame$", MainController::processExitGameCommand, "exitgame"),
  EDIT_MAP("^editmap (.+)$", MainController::processEditMapCommand, "editmap <fileLocation>"),
  EDIT_CONTINENT(
      "^editcontinent( -(add ([^ ]+) (\\d+)|remove ([^ ]+)))+$",
      EditorController::processEditContinentCommand,
      "editcontinent -add <continentName> <controlValue> -remove <continentName>"),
  EDIT_COUNTRY(
      "^editcountry( -(add ([^ ]+) ([^ ]+)|remove ([^ ]+)))+$",
      EditorController::processEditCountryCommand,
      "editcountry -add <countryName> <continentName> -remove <countryName>"),
  EDIT_NEIGHBOR(
      "^editneighbor( -(add|remove) ([^ ]+) ([^ ]+))+$",
      EditorController::processEditNeighborCommand,
      "editneighbor -add <countryName1> <countryName2> -remove <countryName1> <countryName2>"),
  VALIDATE_MAP("^validatemap$", EditorController::processValidateMapCommand, "validatemap"),
  SAVE_MAP("^savemap (.+)$", EditorController::processSaveMapCommand, "savemap <fileLocation>"),
  GAME_PLAYER(
      "^gameplayer( -(add|remove) ([^ ]+))+$",
      MainController::processGamePlayerCommand,
      "gameplayer -add <playerName> -remove <playerName>"),
  LOAD_MAP("^loadmap (.+)$", MainController::processLoadMapCommand, "loadmap <fileLocation>"),
  POPULATE_COUNTRIES(
      "^populatecountries$", SetupController::processPopulateCountriesCommand, "populatecountries"),
  PLACE_ARMY(
      "^placearmy ([^ ]+)$", StartUpController::processPlaceArmyCommand, "placearmy <countryName>"),
  PLACE_ALL("^placeall$", StartUpController::processPlaceAllCommand, "placeall"),
  REINFORCE(
      "^reinforce ([^ ]+) (\\d+)$",
      GameController::processReinforceCommand,
      "reinforce <countryName> <armyCount>"),
  EXCHANGE_CARDS(
      "^exchangecards ((\\d+) (\\d+) (\\d+)|-none)$",
      GameController::processExchangeCardsCommand,
      "exchangecards <num> <num> <num> -none"),
  FORTIFY(
      "^fortify (([^ ]+) ([^ ]+) (\\d+)|-none)?$",
      GameController::processFortifyCommand,
      "fortify <fromCountryName> <toCountryName> <armyCount> -none"),
  ATTACK(
      "^attack (([^ ]+) ([^ ]+) (\\d+|-allout)|-noattack)$",
      GameController::processAttackCommand,
      "attack <fromCountryName> <toCountryName> <numOfDice> -allout -noattack"),
  DEFEND("^defend (\\d+)$", null, "defend <numOfDice>"),
  ATTACK_MOVE("^attackmove (\\d+)$", null, "attackmove <armyCount>"),
  SHOW_MAP("^showmap$", EditorController::processShowMapCommand, "showmap"),
  SHOW_MAP_OWNERSHIP("^showmap$", GameController::processShowMapCommand, "showmap");
  String regex;
  BiPredicate<GameMap, String> operation;
  String usage;

  Command(String regex, BiPredicate<GameMap, String> operation, String usage) {
    this.regex = regex;
    this.operation = operation;
    this.usage = usage;
  }

  public String getRegex() {
    return regex;
  }

  public BiPredicate<GameMap, String> getOperation() {
    return operation;
  }

  public boolean validate(String riskCommand) {
    return riskCommand.matches(regex);
  }

  public String getUsage() {
    return usage;
  }

  public boolean runOperation(GameMap gameMap, String riskCommand) {
    if (validate(riskCommand)) {
      return operation.test(gameMap, riskCommand);
    }
    return false;
  }
}
```
### Context validation of command
The commands allowed in each context of the game are stored in an enum.
Convenience methods like ```validate, getMatchedCommand, runCommand``` are added for ease of use.

Each type contains default commands like 'help', 'exitgame' and also the appropriate commands for that phase.
For eg: When the current context is ```MAIN_MENU``` then only the commands ```EDIT_MAP, GAME_PLAYER, LOAD_MAP, GAME_HELP, EXIT_GAME``` are allowed to be run. If any other command is run the validation fails.


```java
public enum Context {
  MAIN_MENU(EDIT_MAP, GAME_PLAYER, LOAD_MAP),
  MAP_EDITOR(SHOW_MAP, EDIT_CONTINENT, EDIT_COUNTRY, EDIT_NEIGHBOR, VALIDATE_MAP, SAVE_MAP),
  GAME_SETUP(GAME_HELP, SHOW_MAP, EXIT_GAME, GAME_PLAYER, POPULATE_COUNTRIES),
  GAME_STARTUP(SHOW_MAP_OWNERSHIP, PLACE_ARMY, PLACE_ALL),
  GAME_REINFORCE(SHOW_MAP_OWNERSHIP, REINFORCE, EXCHANGE_CARDS),
  GAME_ATTACK(SHOW_MAP_OWNERSHIP, ATTACK),
  GAME_ATTACK_BATTLE_DEFENDER(DEFEND),
  GAME_ATTACK_BATTLE_VICTORY(ATTACK_MOVE),
  GAME_FORTIFY(SHOW_MAP_OWNERSHIP, FORTIFY);
  Command[] validCommands;
  Command[] defaultCommands = {GAME_HELP, EXIT_GAME};

  Context(Command... validCommands) {
    this.validCommands =
        Stream.of(validCommands, defaultCommands).flatMap(Stream::of).toArray(Command[]::new);
  }

  public boolean validate(String riskCommand) {
    return Arrays.stream(validCommands).map(Command::getRegex).anyMatch(riskCommand::matches);
  }

  public Optional<Command> getMatchedCommand(String riskCommand) {
    return Arrays.stream(validCommands).filter(c -> c.validate(riskCommand)).findFirst();
  }

  public boolean runCommand(GameMap gameMap, String riskCommand) {
    for (Command commandValidator : validCommands) {
      if (commandValidator.validate(riskCommand)) {
        return commandValidator.runOperation(gameMap, riskCommand);
      }
    }
    return false;
  }

  public Command[] getValidCommands() {
    return validCommands;
  }
}
```
### Command option validation
In the ```GameController``` class, the process method of each operation is refactored into separate methods for validate and perform.
```java
public static boolean processFortifyCommand(GameMap gameMap, String command) {
  if (command.contains("-none")) {
    return performFortifyNone(gameMap);
  }
  if (validateFortify(gameMap, command)) {
    return performFortify(gameMap, command);
  } else {
    return false;
  }
}

public static boolean validateFortify(GameMap gameMap, String command) {
      Player currentPlayer = gameMap.getCurrentPlayer();
      String[] commandSplit = command.split(" ");
      if (commandSplit.length == 1) {
        return false;
    }

    String fromCountry = commandSplit[1];
    String toCountry = commandSplit[2];
    int armyToMove = Integer.parseInt(commandSplit[3]);
    if (armyToMove < 1) {
      display("Army(s) count is invalid", false);
      return false;
    }

    ArrayList<Country> playerOwnedCountries =
        Player.getCountriesByOwnership(currentPlayer.getPlayerName(), gameMap);

    boolean isOwnershipValid =
        playerOwnedCountries.stream().anyMatch(c -> c.getName().equals(fromCountry))
            && playerOwnedCountries.stream().anyMatch(c -> c.getName().equals(toCountry));
    if (!isOwnershipValid) {
      display(
          String.format(
              "%s doesnt own the country(s) %s, %s or does not exist",
              currentPlayer.getPlayerName(), fromCountry, toCountry),
          false);
      return false;
    }

    boolean isAdjacent = gameMap.getBorders().get(fromCountry).contains(toCountry);
    if (!isAdjacent) {
      display(String.format("%s, %s are not adjacent", fromCountry, toCountry), false);
      return false;
    }

    if (armyToMove >= gameMap.getCountries().get(fromCountry).getNumberOfArmies()) {
      display("Error: entered fortify army count is greater than available armies", false);
      return false;
    }

    return true;
  }

  public static boolean performFortify(GameMap gameMap, String command) {
    Player currentPlayer = gameMap.getCurrentPlayer();

    String[] commandSplit = command.split(" ");
    String fromCountry = commandSplit[1];
    String toCountry = commandSplit[2];
    int armyToMove = Integer.parseInt(commandSplit[3]);
    boolean result = currentPlayer.fortify(gameMap, fromCountry, toCountry, armyToMove);
    if (result) {
      display(
          String.format(
              "%s Fortified %s with %d army(s) from %s",
              currentPlayer.getPlayerName(), toCountry, armyToMove, fromCountry),
          true);
      changeToNextPhase(gameMap);
    } else {
      display(
          String.format("unable to fortify %s country with armies from %s", toCountry, fromCountry),
          false);
    }
    return result;
  }
```
### Split behaviour of each phase into their own controller
Refactor methods from ```GameRunner``` class from *build#1* into methods that are grouped based on the context in which they operate.
```java
class MainController {
    public static boolean processGameHelpCommand(GameMap gameMap, String command){...}
    public static boolean processExitGameCommand(GameMap gameMap, String command){...}
    public static boolean processEditMapCommand(GameMap gameMap, String command){...}
    public static boolean processGamePlayerCommand(GameMap gameMap, String command){...}
}

class EditorController {
    public static boolean processEditContinentCommand(GameMap gameMap, String command){...}
    public static boolean processEditCountryCommand(GameMap gameMap, String command){...}
    public static boolean processEditNeighborCommand(GameMap gameMap, String command){...}
    public static boolean processValidateMapCommand(GameMap gameMap, String command){...}
    public static boolean processSaveMapCommand(GameMap gameMap, String command){...}
    public static boolean processShowMapCommand(GameMap gameMap, String command){...}
}

class SetupController {
    public static boolean processPopulateCountriesCommand(GameMap gameMap, String command){...}
}

class SetupController { 
    public static boolean processPopulateCountriesCommand(GameMap gameMap, String command){...}
}

class StartUpController {
    public static boolean processPlaceArmyCommand(GameMap gameMap, String command){...}
    public static boolean processPlaceAllCommand(GameMap gameMap, String command){...}
}
class GameController {
    public static boolean processReinforceCommand(GameMap gameMap, String command){...}
    public static boolean processExchangeCardsCommand(GameMap gameMap, String command){...}
    public static boolean processFortifyCommand(GameMap gameMap, String command){...}
    public static boolean processAttackCommand(GameMap gameMap, String command){...}
    public static boolean processShowMapCommand(GameMap gameMap, String command){...}
}
```

### Move player actions like attack, fortify, reinforce into Player class
Move methods ```attack, fortify, reinforce``` from ```GameMap``` class into ```Player``` class. So the player object can directly invoke those commands.
```java
class Player {
  ...
  public boolean reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace) {
    return gameMap.placeArmy(countryToPlace, armiesToPlace);
  }

  public boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove) {
    boolean result = false;
    boolean isArmyRemoved = gameMap.getCountries().get(fromCountry).removeArmies(armyToMove);
    if (isArmyRemoved) {
      gameMap.getCountries().get(toCountry).addArmies(armyToMove);
      result = true;
    }
    return result;
  }

  public boolean attack(GameMap gameMap, String command) {
    // Start battle loop
    BattleController battleController = new BattleController(gameMap, command);
    return battleController.startBattle();
  }
}
```  
### Refactor list of possible commands to 'help' command
In *build#1* list of possible commands were manually printed using a hardcoded string. It is now extracted into a custom 'help' command, which can be invoked in any phase.

'help' prints out all valid commands of the current ```Context``` by using ```command.getUsage()``` 
```java
// Before
System.out.println(
          "available commands \n editcontinent -add <continentname> <continentvalue> -remove <continentname> \n editcountry -add <countryname> <continentname> -remove <countryname> \n editneighbor -add <countryname> <neighborcountryname> -remove <countryname> <neighborcountryname> \n validatemap \n showmap \n exiteditor");

      
// After
 public static boolean processGameHelpCommand(GameMap gameMap, String command) {
    Command[] validCommands = gameMap.getCurrentContext().getValidCommands();
    System.out.println("Available Commands:");
    for (Command validCommand : validCommands) {
      display("\t" + validCommand.getUsage(), false);
    }
    return true;
  }
```

### Delegate user feedback to display()
Created a helper function for displaying feedback to the user.
Works with both the GUI and the console to show feedback appropriately.
```java
 public static void display(String text, boolean writeLog) {
    if (writeLog) {
      // Propagate feedback to the GUI
      getGameMap().setPhaseLog(String.format("-> %s\n", text), false);
    }
    System.out.println("-> " + text);
  }
```

### Refactor GameMap class to a singleton that is observable
At any point in the game there is only one instance of ```GameMap```, so it is now refactored into a singleton. Previously ```gameMap``` was stored as a field in the ```CLI``` class
```java
class GameMap extends Observable{
  /**
   * A method to get the existing instance of gameMap, or creating one if it doesn't exist.
   *
   * @return The instance of the gameMap.
   */
  public static GameMap getGameMap() {
    if (gameMap == null) {
      gameMap = new GameMap();
    }
    return gameMap;
  } 
}
```
