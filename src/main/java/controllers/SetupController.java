package controllers;

import models.Context;
import models.GameMap;
import models.player.Player;
import models.player.PlayerHuman;

import static views.ConsoleView.display;

/**
 * Controller for Setup Context
 *
 * @author Sabari
 * @version 1.0
 */
public class SetupController {

  /**
   * Processes populatecountries command from the cli Randomly assigns players to countries in a
   * round robin fashion
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true if populated successfully, false if player count is not valid
   */
  public static boolean processPopulateCountriesCommand(GameMap gameMap, String command) {
      if (gameMap.getPlayersList().size() > gameMap.getCountries().size()) {
          display("No of countries in the map should be > no of players", false);
          return false;
      }
    boolean result = gameMap.gameSetup();
    if (result) {
        display("Populated countries randomly", false);
        gameMap.setCurrentContext(Context.GAME_STARTUP);
        if (!GameController.isTournament && gameMap.getPlayersList().stream().anyMatch(player -> !(player.getStrategy() instanceof PlayerHuman))) {
            display("Executing 'placeall' since there exists an AI player!", true);
            StartUpController.processPlaceAllCommand(gameMap, "placeall");
        } else {
            display("[Game Startup Phase]", false);
            Player currentPlayer = gameMap.getCurrentPlayer();
            display(
                    String.format("%s's turn to place an army", currentPlayer.getStrategy().getPlayerName()),
                    false);
            display(
                    String.format(
                            "%s has %d armies left to place",
                            currentPlayer.getStrategy().getPlayerName(),
                            currentPlayer.getStrategy().getNumberOfArmies()),
                    false);
        }
    } else {
        display("The player list should be > 1 and <= 6", false);
    }
    return result;
  }
}
