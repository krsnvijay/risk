package controllers;

import static controllers.GameController.startPhaseLoop;
import static views.ConsoleView.display;

import models.GameMap;

public class StartUpController {

  public static boolean placeArmy(GameMap gameMap, String command) {
    String[] commandSplit = command.split(" ");
    String countryName = commandSplit[1];
    boolean result = gameMap.placeArmy(countryName, 1);
    if (result) {
      display(gameMap.getCurrentPlayer().getPlayerName() + " placed an army in " + countryName);
      if (gameMap.checkGameReady()) {
        startPhaseLoop(gameMap);
        return true;
      } else {
        gameMap.updatePlayerIndex();
        display(gameMap.getCurrentPlayer().getPlayerName() + " 's turn");
      }
    } else {
      display(
          gameMap.getCurrentPlayer().getPlayerName()
              + " does not own "
              + countryName
              + " to place armies");
    }
    return false;
  }

  public static boolean placeAll(GameMap gameMap, String command) {
    gameMap.placeAll();
    display("Placed player armies randomly in countries that they own");
    startPhaseLoop(gameMap);
    return false;
  }
}
