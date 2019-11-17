# Coding Conventions
We follow consistent coding standards in our team.

Most standard Java conventions are used. Variables are camel cased and class names are proper singular nouns. Constants are named using capitals and underscores. White space after each declaration and definition is present. A 2-space model is followed for indentation, as defined by Google. Comments are spell-checked and maintained for clarity of code. All code is documented using Javadoc, including private instance members.

We strive for high code quality. To ensure our code stays readable, brief but descriptive names are used for variables and methods. Utility methods are separated modules to ensure reusability and maintainability. We almost always work using Pair Programming to ensure all members are aware of all the sections of the code, as well as having a consensus on the methodology of the code we write.

## Code Layout
## Column limit: 100
Avoid lines longer than 100 characters, since they're not handled well by many terminals and tools.

When an expression will not fit on a single line, break it after a comma or before an operator. 
```java
// Correct

    String neighbors =
        gameBorders.get(countryName).stream()
            .map(countriesOrder::indexOf)
            .map(i -> i + 1)
            .sorted()
            .map(String::valueOf)
            .collect(joining(" "));
```
```java
// Incorrect

    String neighbors = gameBorders.get(countryName).stream().map(countriesOrder::indexOf).map(i -> i + 1).sorted().map(String::valueOf).collect(joining(" "));
```
## One statement per line
Each statement is followed by a line break.
```java
// Correct

  String commandType = subCommandSplit[0];
  String countryName = subCommandSplit[1];
```
```java
// Incorrect

  String commandType = subCommandSplit[0];String countryName = subCommandSplit[1];
```
## Braces are used where optional
Braces are used with if, else, for, do and while statements, even when the body is empty or contains only a single statement.
```java
// Correct

if (result) {
  display("Reinforce successful");
} else {
  display("Reinforce Failed");
}

for (Country country:countries) {
  display(country);
}

while (result) {
  display(gameMap.getCurrentPlayer().getNumOfArmies());
}
```
```java
// Incorrect

if (result)
  display("Reinforce successful");
else
  display("Reinforce Failed");

for (Country country:countries)
  display(country);

while (result)
  display(gameMap.getCurrentPlayer().getNumOfArmies());
```
## Indentation
Each time a new block or block-like construct is opened, the indent increases by two spaces. When the block ends, the indent returns to the previous indent level. The indent level applies to both code and comments throughout the block.

### Block indentation: +2 spaces
```java
// Correct

public int calculateReinforcements(GameMap gameMap) {
  int ownedCountries = getCountriesByOwnership(this.playerName, gameMap).size();
  int allReinforcementArmies = getBonusArmiesIfPlayerOwnsContinents(playerName, gameMap);
  if (ownedCountries < 9) {
    allReinforcementArmies += 3;
  }
  allReinforcementArmies += ownedCountries / 3;
  return allReinforcementArmies;
}
```
```java
// Incorrect

public int calculateReinforcements(GameMap gameMap) {
    int ownedCountries = getCountriesByOwnership(this.playerName, gameMap).size();
    int allReinforcementArmies = getBonusArmiesIfPlayerOwnsContinents(playerName, gameMap);
    if (ownedCountries < 9) {
        allReinforcementArmies += 3;
    }
    allReinforcementArmies += ownedCountries / 3;
    return allReinforcementArmies;
}
```

### Indent continuation +4 space
When line-wrapping, each line after the first (each continuation line) is indented at least +4 from the original line.
```java
// Correct

ArrayList<Country> countriesOwnedByCurrPlayer =
    this.countries.values().stream()
        .filter(country -> country.getOwnerName().equals(currentPlayer))
        .collect(toCollection(ArrayList::new));
```
```java
// Incorrect

ArrayList<Country> countriesOwnedByCurrPlayer =
  this.countries.values().stream()
    .filter(country -> country.getOwnerName().equals(currentPlayer))
    .collect(toCollection(ArrayList::new));
```
## One variable per declaration
Every variable declaration (field or local) declares only one variable
```java
// Correct

int numOfDiceAttacker;
int numOfDiceDefender;
```
```java
// Incorrect

int numOfDiceAttacker, numOfDiceDefender;
```
# Spacing
## Vertical Whitespace
Blank lines improve readability by setting off sections of code that are logically related.

A single blank line may also appear anywhere it improves readability.
```java
// Correct

class Player {

  private String name;
  private int armyCount;

  public Player(String name, int armyCount) {
    ...
  }
    
  public addArmies(int armyCount) {
    boolean isCountValid = armyCount > 0;

    if (isCountValid > 0) {
      this.armyCount += armyCount;
    }
  }
}
```
```java
// Incorrect

class Player {
  private String name;
  private int armyCount;
  public Player(String name, int armyCount) {
    ...
  }
  public addArmies(int armyCount) {
    boolean isCountValid = armyCount > 0;
    if (isCountValid > 0) {
      this.armyCount += armyCount;
    }
  }
}
```
## Horizontal Whitespace
1. A keyword followed by a parenthesis should be separated by a space.

2. A blank space should appear after commas in argument lists. 

3. All binary operators except . should be separated from their operands by spaces.
 
4. Blank spaces should never separate unary operators such as unary minus, increment ("++"), and decrement ("--") from their operands.
```java
// Correct

if (numOfArmy > 2) {
  ...
}

performAttack(gameMap, command);

count += calculateReinforcements();

currentPlayerIndex++;
```
```java
// Incorrect

if(numOfArmy > 2){
  ...
}

performAttack(gameMap,command);

count+=calculateReinforcements();

currentPlayerIndex ++;
```
# Naming
Rules common to all identifiers Identifiers use only ASCII letters and digits, and, in a small number of cases noted below, underscores
## Class names - UpperCamelCase
Class names are typically nouns or noun phrases
```java
// Correct

class BattleController {
  ...
}
```
```java
// Incorrect

class Battle_Controller {
  ...
}

class battleController {
  ...
}

class BATTLECONTROLLER {
  ...
}
```

## Method names - lowerCamelCase
Method names are typically verbs or verb phrase
```java
// Correct

public int getNumOfDiceFromAttacker() {
  ...
}

public boolean validate(String riskCommand) {
  ...
}
```
```java
\\ Incorrect
public int get_num_of_dice_from_attacker() {
  ...
}

public boolean VALIDATE(String riskCommand) {
  ...
}
```
## Constant names - CONSTANT_CASE
Constants are immutable fields. Use all uppercase letters, with each word separated from the next by a single underscore
```java
// Correct

static final String COUNTRY_1 = "India";
static final int MAX_NUM_OF_DICE = 3;
enum Context { GAME_SETUP, GAME_REINFORCE, ... }
```
```java
// Incorrect

static final String Country_1 = "India";
static final int maxNumOfDice = 3;
enum Context { game_setup, GaMeReInFoRcE, ... }
```
## Parameter names - lowerCamelCase
use variable names that are relevant to the operation that they perform
```java
// Correct

  public boolean placeArmy(String countryName, int numArmies) {
  ...
}
```
```java
// Incorrect

  public boolean placeArmy(String COUNTRY_NAME, int num_armies) {
  ...
}
```
## Local variable names - lowerCamelCase
Use meaningful names that describe the value they are storing. Don't use vague names like x, y, name1 etc.
```java
// Correct

Country attackingCountry = gameMap.getCountries().get(commandSplit[1]);
String attackerName = attackingCountry.getOwnerName();
Country defendingCountry = gameMap.getCountries().get(commandSplit[2]);
String defenderName = defendingCountry.getOwnerName();
```
```java
// Incorrect

Country x = gameMap.getCountries().get(commandSplit[1]);
String ATTACKERNAME = attackingCountry.getOwnerName();
Country defending_country = gameMap.getCountries().get(commandSplit[2]);
String dEfEnDeRnAmE = defendingCountry.getOwnerName();
```


## Exactly one top-level class
Each java file should only contain one top-level class.
```java
// Correct
// File GameMap.java
Class GameMap{
  
  ...
}
PlayerHuman.java
Class Player{
  ...
}
```
```java
// Incorrect
// File GameMap.java
Class GameMap{
  ...
}
Class Player{
  ...
}
```
## Comments
Don't over comment, Write code that is self-explanatory, use meaningful names for classes, methods, variables to express your code.

Write comments only when the method is too lengthy or logic is too complicated for a developer to understand.

No commented code, delete it if its outdated otherwise use git to revert if needed.
```java
// Correct

public static boolean performAttack(GameMap gameMap, String command) {
    Player currentPlayer = gameMap.getCurrentPlayer();
    String[] commandSplit = command.split(" ");
    Country attackingCountry = gameMap.getCountries().get(commandSplit[1]);
    String attackerName = attackingCountry.getOwnerName();
    Country defendingCountry = gameMap.getCountries().get(commandSplit[2]);
    String defenderName = defendingCountry.getOwnerName();
    // Start battle
    display(
        String.format(
            "%s owned by %s declared an attack on %s owned by %s",
            attackingCountry.getName(), attackerName, defendingCountry.getName(), defenderName),
        true);
    return currentPlayer.attack(gameMap, command);
  }
```
```java
// Incorrect

// public static int checkWinCondition(GameMap gameMap) {
//  if(gameMap.getPlayersList().size() == 0) {
//    return true;
//  }
//  return false;
//}

public static boolean performAttack(GameMap gameMap, String command) {
  // Get current player
  Player player = gameMap.getCurrentPlayer();
  // Split command string
  String[] array = command.split(" ");
  // Get attacking country object from its name
  Country country1 = gameMap.getCountries().get(commandSplit[1]);
  // Get attacking country owner
  String player1 = country1.getOwnerName();
  // Get defending country from command
  Country defendingCountry = gameMap.getCountries().get(commandSplit[2]);
  // Get defending country owner
  String defenderName = defendingCountry.getOwnerName();
  // Start battle
  display(
      String.format(
          "%s owned by %s declared an attack on %s owned by %s",
          country1.getName(), player1, defendingCountry.getName(), defenderName),
      true);
  return player.attack(gameMap, command);
  }
```
## JavaDoc 
For all files, classes and methods, including private members. All test classes and test cases properly documented.

```java
/**
 * This class maintains the information for a Country.
 *
 */
public class Country {
  /** The name of the continent the Country belongs to. */
  private String continent;
  /** The name of the Country. */
  private String name;

  /**
   * The constructor for the Country class.
   *
   * @param name The name of the Country.
   * @param continent The name of the Continent it belongs to.
   */
  public Country(String name, String continent) {
    super();
    this.name = name;
    this.continent = continent;
  }

  /**
   * Returns the name of the continent.
   *
   * @return String with the name.
   */
  public String getContinent() {
    return this.continent;
  }
}
```