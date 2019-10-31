package controllers;

import models.Context;
import models.Country;
import models.GameMap;
import models.Player;
import utils.CLI;

import java.util.ArrayList;

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
              gameMap.getCurrentPlayer().getPlayerName(), armiesToPlace, countryName));
    } else {
      display(
          String.format(
              "%s doesnt own %s or it does not exist",
              gameMap.getCurrentPlayer().getPlayerName(), countryName));
    }
    if (gameMap.getCurrentPlayer().getNumberOfArmies() == 0) {
      changeToNextPhase(gameMap);
    } else {
      display(
          String.format(
              "%s has %d army(s) to reinforce",
              gameMap.getCurrentPlayer().getPlayerName(),
              gameMap.getCurrentPlayer().getNumberOfArmies()));
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
      display(String.format("%s chose not to fortify", gameMap.getCurrentPlayer().getPlayerName()));
      changeToNextPhase(gameMap);
      return true;
    }
    String[] commandSplit = command.split(" ");
    String fromCountry = commandSplit[1];
    String toCountry = commandSplit[2];
    int armyToMove = Integer.parseInt(commandSplit[3]);
    if (armyToMove < 0) {
      display("Army(s) count is invalid");
      return false;
    }
    boolean result = gameMap.fortify(fromCountry, toCountry, armyToMove);
    if (result) {
      display(
          String.format(
              "Fortified %s with %d army(s) from %s", toCountry, armyToMove, fromCountry));
      changeToNextPhase(gameMap);
    } else {
      display(
          String.format(
              "%s doesnt own the country(s) %s, %s or  does not exist",
              gameMap.getCurrentPlayer().getPlayerName(), fromCountry, toCountry));
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
    display(gameMap.showMapByOwnership());
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
        display("[Attack]");
        break;
      case GAME_ATTACK:
        gameMap.setCurrentContext(Context.GAME_FORTIFY);
        display("[Fortify]");
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
    display(gameMap.getCurrentPlayer().getPlayerName() + "'s turn:");
    display("[Reinforce]");
    display(
        String.format(
            "%s has %d army(s) to reinforce",
            gameMap.getCurrentPlayer().getPlayerName(),
            gameMap.getCurrentPlayer().getNumberOfArmies()));
  }

  /**
   * Skips attack for the current player
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true to indicate status
   */
  public static boolean attack(GameMap gameMap, String command) {
    // Store defendingCountryName in gameMap
    // If no attack set it to empty string
    // get owner of defendingCountryName
    // Allow defend command for defending Player
    if (command.contains("-noattack")) {
      display(String.format("%s chose not to attack", gameMap.getCurrentPlayer().getPlayerName()));
      changeToNextPhase(gameMap);
      return true;
    } else if (command.contains("-allout")) {

      // Attack till you win or have no armies to attack
      // Requires no input from attacker but defender needs to specify dice
      // Attacker cannot abandon an ongoing battle
      // Allow defender to make their move
      // Simulate Roll dice
      // Remove armies by comparing dices
      // If Attacker wins the battle they can move armies to their new territory and plan another
      // attack
      // Check for Game Victory Condition here
      // If Attack not possible move automatically to fortify

    }
    // Initial Attack Phase - Choose what to attack or stop attack phase and move to fortify
    // Start Battle loop or stop attack phase and move to fortify
    // Requires input from both the attacker and the defender after every attack move
    // Attacker can abandon an ongoing battle and plan another attack
    // Allow defender to make their move
    // Todo get player names
    Country defendingCountry = gameMap.getCountries().get("");
    Country attackingCountry = gameMap.getCountries().get("");
    String attackerName = gameMap.getCurrentPlayer().getPlayerName();
    String defenderName = defendingCountry.getOwnerName();
    int numOfDiceAttacker = 1;
    int numOfDiceDefender = 1;

    // Player can only attack if he has atleast two armies in a country he owns
    boolean isAttackPossible =
        Player.getCountriesByOwnership(attackerName, gameMap).stream()
            .mapToInt(Country::getNumberOfArmies)
            .anyMatch(armyCount -> armyCount > 1);
    if (!isAttackPossible) {
      //If Attack in the whole map is not possible move automatically to fortify
      gameMap.setCurrentContext(Context.GAME_FORTIFY);
      return true;
    }
    // Roll dice for attacker
    int[] attackerDiceRoll = GameMap.rollDice(numOfDiceAttacker);
    // Roll dice for defender
    int[] defenderDiceRoll = GameMap.rollDice(numOfDiceDefender);
    // Compare diceRoll results
    ArrayList<Boolean> results = GameMap.compareDiceRolls(attackerDiceRoll, defenderDiceRoll);
    for (boolean result : results) {
      if (result) {
        if (defendingCountry.getNumberOfArmies() > 1) {
          // Attack successful: Remove army from defender
          defendingCountry.removeArmies(1);
        } else if (defendingCountry.getNumberOfArmies() == 1) {
          // Attacker Won the battle and conquered the defending country
          defendingCountry.removeArmies(1);
          // Change ownership to attacker
          defendingCountry.setOwnerName(attackerName);
          // Change context to Battle victory
          gameMap.setCurrentContext(Context.GAME_ATTACK_BATTLE_VICTORY);
          // Check Game Victory condition
          if (gameMap.checkGameVictory()) {
            // Player Won the Game, Exit
            display("Victory!");
            System.exit(0);
          } else {
            // Change to initial attack phase where player can choose to battle again
            gameMap.setCurrentContext(Context.GAME_ATTACK);
          }
        }
      } else {
        if (attackingCountry.getNumberOfArmies() > 1) {
          // Defend successful: Remove army from attacker
          attackingCountry.removeArmies(1);
        } else if (attackingCountry.getNumberOfArmies() == 1) {
          // Remove last army from attacking country
          attackingCountry.removeArmies(1);
          // Attacker has no armies left to attack. Stop battle
          // set defendingCountryName in gameMap to empty
          gameMap.setDefendingCountryName("");
          // Change to initial attack phase where player can choose to battle again
          gameMap.setCurrentContext(Context.GAME_ATTACK);
        }
      }
    }
    return true;
  }

  public static boolean defend(GameMap gameMap, String s) {
    return true;
  }

  public static boolean attackMove(GameMap gameMap, String s) {
    return true;
  }
}
