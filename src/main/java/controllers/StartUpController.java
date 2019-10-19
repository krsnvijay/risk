package controllers;

import static controllers.GameController.startPhaseLoop;
import static views.ConsoleView.display;

import models.GameMap;

/**
 * Controller for StartUp context
 */
public class StartUpController {

  /**
   * Processes placearmy command from the cli Adds an army to a country if the user owns it
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true if successfully placed an army
   */
  public static boolean placeArmy(GameMap gameMap, String command) {
    String[] commandSplit = command.split(" ");
    String countryName = commandSplit[1];
    boolean result = gameMap.placeArmy(countryName, 1);
    if (result) {
      display(
          String.format(
              "%s placed an army in %s", gameMap.getCurrentPlayer().getPlayerName(), countryName));
      if (gameMap.checkGameReady()) {
        startPhaseLoop(gameMap);
        return true;
      } else {
        gameMap.updatePlayerIndex();
        display(String.format("%s's turn", gameMap.getCurrentPlayer().getPlayerName()));
      }
    } else {
      display(
          String.format(
              "%s does not own %s to place armies or it does not exist",
              gameMap.getCurrentPlayer().getPlayerName(), countryName));
    }
    return result;
  }

  /**
   * Processes placeall command from the cli
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true to indicate status
   */
  public static boolean placeAll(GameMap gameMap, String command) {
    gameMap.placeAll();
    display("Placed player armies randomly in countries that they own");
    startPhaseLoop(gameMap);
    return true;
  }
}
