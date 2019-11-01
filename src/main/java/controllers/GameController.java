package controllers;

import models.Context;
import models.Country;
import models.GameMap;
import models.Player;

import static views.ConsoleView.display;

/**
 * Controller for Game loop and phases
 */
public class GameController {

  /**
   * Processes reinforce command from the cli
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true if successfully reinforced
   */
  public static boolean reinforce(GameMap gameMap, String command) {
    String[] commandSplit = command.split(" ");
    String countryName = commandSplit[1];
    int armiesToPlace = Integer.parseInt(commandSplit[2]);
    boolean result = gameMap.reinforce(countryName, armiesToPlace);
    if (result) {
      display(
          String.format(
              "%s has placed %d army(s) in %s",
              gameMap.getCurrentPlayer().getPlayerName(), armiesToPlace, countryName), true);
    } else {
      display(
          String.format(
              "%s doesnt own %s or it does not exist",
              gameMap.getCurrentPlayer().getPlayerName(), countryName), false);
    }
    if (gameMap.getCurrentPlayer().getNumberOfArmies() == 0) {
      changeToNextPhase(gameMap);
    } else {
      display(
          String.format(
              "%s has %d army(s) to reinforce",
              gameMap.getCurrentPlayer().getPlayerName(),
              gameMap.getCurrentPlayer().getNumberOfArmies()), true);
    }
    return result;
  }

  /**
   * Processes fortify command from the cli
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true if successfully fortified
   */
  public static boolean fortify(GameMap gameMap, String command) {
    if (command.contains("-none")) {
      display(String.format("%s chose not to fortify", gameMap.getCurrentPlayer().getPlayerName()), true);
      changeToNextPhase(gameMap);
      return true;
    }
    String[] commandSplit = command.split(" ");
    if (commandSplit.length == 1) return false;

    String fromCountry = commandSplit[1];
    String toCountry = commandSplit[2];
    int armyToMove = Integer.parseInt(commandSplit[3]);
    if (armyToMove < 0) {
      display("Army(s) count is invalid", false);
      return false;
    }
    boolean result = gameMap.fortify(fromCountry, toCountry, armyToMove);
    if (result) {
      display(
          String.format(
              "Fortified %s with %d army(s) from %s", toCountry, armyToMove, fromCountry), true);
      changeToNextPhase(gameMap);
    } else {
      display(
          String.format(
              "%s doesnt own the country(s) %s, %s or  does not exist",
              gameMap.getCurrentPlayer().getPlayerName(), fromCountry, toCountry), false);
    }
    return result;
  }

  /**
   * Shows map connectivity, ownership, army(s)
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true to indicate status
   */
  public static boolean showMap(GameMap gameMap, String command) {
    display(gameMap.showMapByOwnership(), false);
    return true;
  }

  /**
   * Changes to next phase in the game loop or changes player if in the last phase of current playe
   *
   * @param gameMap contains game state
   */
  public static void changeToNextPhase(GameMap gameMap) {
    Context currentContext = gameMap.getCurrentContext();
    switch (currentContext) {
      case GAME_REINFORCE:
        gameMap.setCurrentContext(Context.GAME_ATTACK);
        display("[Attack]", false);
        break;
      case GAME_ATTACK:
        gameMap.setCurrentContext(Context.GAME_FORTIFY);
        display("[Fortify]", false);
        break;
      case GAME_FORTIFY:
        gameMap.updatePlayerIndex();
        startPhaseLoop(gameMap);
        break;
    }
  }

  /**
   * Starts the game loop
   *
   * @param gameMap contains game state
   */
  public static void startPhaseLoop(GameMap gameMap) {
    gameMap.setCurrentContext(Context.GAME_REINFORCE);
    gameMap
        .getCurrentPlayer()
        .setNumberOfArmies(gameMap.getCurrentPlayer().calculateReinforcements(gameMap));
    display(gameMap.getCurrentPlayer().getPlayerName() + "'s turn:", false);
    display("[Reinforce]", false);
    display(
        String.format(
            "%s has %d army(s) to reinforce",
            gameMap.getCurrentPlayer().getPlayerName(),
            gameMap.getCurrentPlayer().getNumberOfArmies()), true);
  }

  /**
   * Skips attack for the current player
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true to indicate status
   */
  public static boolean attack(GameMap gameMap, String command) {
    if (command.contains("-noattack")) {
      display(String.format("%s chose not to attack", gameMap.getCurrentPlayer().getPlayerName()), true);
      changeToNextPhase(gameMap);
      return true;
    }
    String[] commandSplit = command.split(" ");
    if (commandSplit.length == 1) return false;
    if (!gameMap.getCountries().containsKey(commandSplit[1])
        || !gameMap.getCountries().containsKey(commandSplit[2])) {
      display("Error: One or both the countries do not exist", false);
      return false;
    }

    Country attackingCountry = gameMap.getCountries().get(commandSplit[1]);
    String attackerName = attackingCountry.getOwnerName();
    Country defendingCountry = gameMap.getCountries().get(commandSplit[2]);
    String defenderName = defendingCountry.getOwnerName();
    String currentPlayer = gameMap.getCurrentPlayer().getPlayerName();

    // Current Player should be owner of the from country
    if (!attackerName.equals(gameMap.getCurrentPlayer().getPlayerName())) {
      display(
          String.format(
              "Error: Current player %s should be owner of %s to attack",
              currentPlayer, attackingCountry.getName()), false);
      return false;
    }
    // Player can't attack their own country
    if (attackerName.equals(defenderName)) {
      display("Error: Player can't attack their own country", false);
      return false;
    }

    // Both from , to country must be adjacent
    if (!gameMap
        .getBorders()
        .get(attackingCountry.getName())
        .contains(defendingCountry.getName())) {
      display(
          String.format(
              "Error: %s and %s need to be adjacent for attack",
              attackingCountry.getName(), defendingCountry.getName()), false);
      return false;
    }
    int numOfDiceAttacker = Integer.parseInt(commandSplit[3]);
    // numOfDice for attacker can't be > 5
    if (numOfDiceAttacker > 5 || numOfDiceAttacker < 1) {
      display("Error: numOfDice must be between 1-5", false);
      return false;
    }
    // for numOfDice allowed value is one less than the numOfArmies
    if (!(numOfDiceAttacker < attackingCountry.getNumberOfArmies())) {
      display("Error: numOfDice should always be one less than numOfArmies", false);
      return false;
    }

    // Player can only attack if he has atleast two armies in a country he owns
    boolean isAttackPossible =
        Player.getCountriesByOwnership(attackerName, gameMap).stream()
            .mapToInt(Country::getNumberOfArmies)
            .anyMatch(armyCount -> armyCount > 1);
    if (!isAttackPossible) {
      // If Attack in the whole map is not possible move automatically to fortify
      display("No possible attack left, changing to next phase", true);
      gameMap.setCurrentContext(Context.GAME_FORTIFY);
      return true;
    }
    BattleController battleController = new BattleController(gameMap, command);
    display(
        String.format(
            "%s owned by %s declared an attack on %s owned by %s",
            attackingCountry.getName(), attackerName, defendingCountry.getName(), defenderName),true);
    return battleController.startBattle(command.contains("-allout"));
  }

  public static boolean defend(GameMap gameMap, String s) {
    return true;
  }

  public static boolean attackMove(GameMap gameMap, String s) {
    return true;
  }
}
