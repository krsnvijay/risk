package controllers;

import static views.ConsoleView.display;

import models.Context;
import models.GameMap;
import utils.CLI;

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
      display("Populated countries randomly");
      CLI.getInstance().setCurrentContext(Context.GAME_STARTUP);
      display("[Game Startup Phase]");
      display(gameMap.getCurrentPlayer().getPlayerName() + " 's turn");
    } else {
      display("The player list should be > 1 and < 6");
    }
    return result;
  }
}
