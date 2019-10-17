package controllers;

import static views.ConsoleView.display;

import models.Context;
import models.GameMap;
import utils.CLI;

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
   * Skips fortification for the current player
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true to indicate status
   */
  public static boolean fortifyNone(GameMap gameMap, String command) {
    display(String.format("%s chose not to fortify", gameMap.getCurrentPlayer().getPlayerName()));
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
    Context currentContext = CLI.getInstance().getCurrentContext();
    switch (currentContext) {
      case GAME_REINFORCE:
        CLI.getInstance().setCurrentContext(Context.GAME_ATTACK);
        display("[Attack]");
        break;
      case GAME_ATTACK:
        CLI.getInstance().setCurrentContext(Context.GAME_FORTIFY);
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
    CLI.getInstance().setCurrentContext(Context.GAME_REINFORCE);
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
  public static boolean attackNone(GameMap gameMap, String command) {
    display(String.format("%s chose not to attack", gameMap.getCurrentPlayer().getPlayerName()));
    changeToNextPhase(gameMap);
    return true;
  }
}
