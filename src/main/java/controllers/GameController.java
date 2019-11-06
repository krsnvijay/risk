package controllers;

import models.Context;
import models.Country;
import models.GameMap;
import models.Player;

import java.util.ArrayList;

import static views.ConsoleView.display;

/**
 * Controller for Game loop and phases
 *
 * @author Warren
 * @version 1.0
 */
public class GameController {

  /**
   * This boolean is false if a card hasn't been assigned to the current player.
   */
  public static boolean assignedCard = false;

  /**
   * Processes reinforce command from the cli
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true if successfully reinforced
   */
  public static boolean processReinforceCommand(GameMap gameMap, String command) {
    if (validateReinforce(gameMap, command)) {
      return performReinforce(gameMap, command);
    } else {
      return false;
    }
  }

  /**
   * process attack command from the cli
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true to indicate status
   */
  public static boolean processAttackCommand(GameMap gameMap, String command) {
    if (command.contains("-noattack")) {
      return performAttackNone(gameMap);
    }

    if (validateAttack(gameMap, command)) {
      return performAttack(gameMap, command);
    } else {
      return false;
    }
  }

  /**
   * Processes fortify command from the cli
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true if successfully fortified
   */
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

  /**
   * Processes exchangecards command from CLI
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true if successfully exchanged cards
   */
  public static boolean processExchangeCardsCommand(GameMap gameMap, String command) {
    if (command.contains("-none")) {
      display(
          String.format(
              "%s doesn't want to exchange cards", gameMap.getCurrentPlayer().getPlayerName()),
          true);
      return true;
    }
    String[] commandSplit = command.split(" ");
    int[] positionOfCards = new int[3];
    for (int i = 1; i < commandSplit.length; i++) {
      positionOfCards[i - 1] = Integer.parseInt(commandSplit[i]);
    }
    // exchange cards method implementation
    gameMap.getCurrentPlayer().exchangeCardsForArmies(positionOfCards);
    return true;
  }

  /**
   * Performs the reinforce command.
   *
   * @param gameMap the Game Map instance.
   * @param command the command entered by the user.
   * @return the result of reinforcement.
   */
  public static boolean performReinforce(GameMap gameMap, String command) {
    Player currentPlayer = gameMap.getCurrentPlayer();
    String[] commandSplit = command.split(" ");
    String countryName = commandSplit[1];
    int armiesToPlace = Integer.parseInt(commandSplit[2]);
    boolean result = currentPlayer.reinforce(gameMap, countryName, armiesToPlace);
    if (result) {
      display(
          String.format(
              "%s has placed %d army(s) in %s",
              currentPlayer.getPlayerName(), armiesToPlace, countryName),
          true);
    } else {
      display(
          String.format(
              "%s doesnt own %s or it does not exist", currentPlayer.getPlayerName(), countryName),
          false);
    }
    if (currentPlayer.getNumberOfArmies() == 0) {
      if (gameMap.getCurrentPlayer().getCardsInHand().size() < 5) {
        changeToNextPhase(gameMap);
      } else {
        display(
            String.format(
                "%s has more than 5 cards. Please exchange cards to continue.",
                currentPlayer.getPlayerName()),
            true);
      }
    } else {
      display(
          String.format(
              "%s has %d army(s) to reinforce",
              currentPlayer.getPlayerName(), currentPlayer.getNumberOfArmies()),
          true);
    }
    return result;
  }

  /**
   * Checks whether fortification is valid.
   * @param gameMap The Game Map instance.
   * @param command The command entered by the user.
   * @return true if the fortification is valid.
   */
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

  /**
   * This method performs the fortification.
   *
   * @param gameMap The Game Map instance.
   * @param command The command entered by the user.
   * @return the boolean result of the fortification.
   */
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

  /**
   * Handles the fortifynone logic.
   * @param gameMap The Game Map instance
   * @return the result of the command.
   */
  private static boolean performFortifyNone(GameMap gameMap) {
    Player currentPlayer = gameMap.getCurrentPlayer();
    display(String.format("%s chose not to fortify", currentPlayer.getPlayerName()), true);
    changeToNextPhase(gameMap);
    return true;
  }

  /**
   * Shows map connectivity, ownership, army(s)
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true to indicate status
   */
  public static boolean processShowMapCommand(GameMap gameMap, String command) {
    display(gameMap.showMapByOwnership(), false);
    return true;
  }

  /**
   * Changes to next phase in the game loop or changes player if in the last phase of current player
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
   * Starts the game loop of the current player
   *
   * @param gameMap contains game state
   */
  public static void startPhaseLoop(GameMap gameMap) {
    gameMap.setCurrentContext(Context.GAME_REINFORCE);
    Player currentPlayer = gameMap.getCurrentPlayer();
    currentPlayer.setNumberOfArmies(currentPlayer.calculateReinforcements(gameMap));
    display(currentPlayer.getPlayerName() + "'s turn:", false);
    display("[Reinforce]", false);
    display(
        String.format(
            "%s has %d army(s) to reinforce",
            currentPlayer.getPlayerName(), currentPlayer.getNumberOfArmies()),
        true);
  }

  /**
   * Validates the attack method.
   * @param gameMap The Game Map instance.
   * @param command The command entered by the user.
   * @return true if the attack is valid, false otherwise.
   */
  public static boolean validateAttack(GameMap gameMap, String command) {
    Player currentPlayer = gameMap.getCurrentPlayer();

    // Validate country names
    String[] commandSplit = command.split(" ");
    if (commandSplit.length == 1) {
      return false;
    }
    if (!gameMap.getCountries().containsKey(commandSplit[1])
        || !gameMap.getCountries().containsKey(commandSplit[2])) {
      display("Error: One or both the countries do not exist", false);
      return false;
    }

    // Get Country object and their owners
    Country attackingCountry = gameMap.getCountries().get(commandSplit[1]);
    String attackerName = attackingCountry.getOwnerName();
    Country defendingCountry = gameMap.getCountries().get(commandSplit[2]);
    String defenderName = defendingCountry.getOwnerName();
    // Current Player should be owner of the from country
    if (!attackerName.equals(currentPlayer.getPlayerName())) {
      display(
          String.format(
              "Error: Current player %s should be owner of %s to attack",
              currentPlayer, attackingCountry.getName()),
          false);
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
              attackingCountry.getName(), defendingCountry.getName()),
          false);
      return false;
    }

    // Validate dice of Attacker if allout mode is not enabled
    if (!command.contains("-allout")) {
      int numOfDiceAttacker = Integer.parseInt(commandSplit[3]);
      // numOfDice for attacker can't be > 3
      if (numOfDiceAttacker > 3 || numOfDiceAttacker < 1) {
        display("Error: numOfDice must be between 1-3", false);
        return false;
      }
      // for numOfDice allowed value is one less than the numOfArmies
      if (!(numOfDiceAttacker < attackingCountry.getNumberOfArmies())) {
        display("Error: numOfDice should always be one less than numOfArmies", false);
        return false;
      }
    }

    // Needs 2 armies in from country to attack
    if (attackingCountry.getNumberOfArmies() < 2) {
      display("Need atleast 2 armies to attack", false);
      return false;
    }

    return true;
  }

  /**
   * This method performs the attack.
   *
   * @param gameMap The Game Map instance.
   * @param command The command entered by the user.
   * @return The boolean result of the attack command.
   */
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

  /**
   * This method handles the attack none scenario.
   *
   * @param gameMap The Game Map instance.
   * @return the boolean result of the attacknone command.
   */
  public static boolean performAttackNone(GameMap gameMap) {
    Player currentPlayer = gameMap.getCurrentPlayer();
    display(String.format("%s chose not to attack", currentPlayer.getPlayerName()), true);
    if (GameController.assignedCard) {
      GameController.assignedCard = false;
    }
    changeToNextPhase(gameMap);
    return true;
  }

  /**
   * Validates the reinforcement command.
   *
   * @param gameMap The Game Map instance.
   * @param command The command entered by the user.
   * @return
   */
  public static boolean validateReinforce(GameMap gameMap, String command) {
    Player currentPlayer = gameMap.getCurrentPlayer();

    String[] commandSplit = command.split(" ");
    String countryName = commandSplit[1];
    int armiesToPlace = Integer.parseInt(commandSplit[2]);

    if (Player.getCountriesByOwnership(currentPlayer.getPlayerName(), gameMap).stream()
        .noneMatch(c -> c.getName().equals(countryName))) {
      display("Player doesnt own the country or it doesnt exist", false);
      return false;
    }

    if (armiesToPlace < 1 || armiesToPlace > currentPlayer.getNumberOfArmies()) {
      display("numOfArmy should be > 0 and < available armies", false);
      return false;
    }
    return true;
  }
}
