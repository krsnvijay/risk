package controllers;

import static views.ConsoleView.display;

import models.Context;
import models.GameMap;
import models.Player;

/**
 * Controller for Setup Context
 */
public class SetupController {

  /**
   * Processes populatecountries command from the cli Randomly assigns players to countries in a
   * round robin manner
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true if populated successfully, false if player count is not valid
   */
  public static boolean populateCountries(GameMap gameMap, String command) {
    boolean result = gameMap.gameSetup();
    if (result) {
      display("Populated countries randomly", false);
      gameMap.setCurrentContext(Context.GAME_STARTUP);
      display("[Game Startup Phase]", false);
      Player currentPlayer = gameMap.getCurrentPlayer();
      display(String.format("%s's turn to place an army", currentPlayer.getPlayerName()), false);
      display(
          String.format(
              "%s has %d armies left to place",
              currentPlayer.getPlayerName(), currentPlayer.getNumberOfArmies()),
          false);
    } else {
      display("The player list should be > 1 and <= 6", false);
    }
    return result;
  }
}
