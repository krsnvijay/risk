package controllers;

import static views.ConsoleView.display;

import models.Context;
import models.GameMap;
import utils.CLI;

public class GameController {

  public static boolean reinforce(GameMap gameMap, String command) {
    String[] commandSplit = command.split(" ");
    String countryName = commandSplit[1];
    int armiesToPlace = Integer.parseInt(commandSplit[2]);
    boolean result = gameMap.reinforce(countryName, armiesToPlace);
    if (result) {
      display(
          gameMap.getCurrentPlayer().getPlayerName()
              + " has placed "
              + armiesToPlace
              + " army(s) in "
              + countryName);
    } else {
      display("player doesnt own the country or it does not exist in map");
    }
    if (gameMap.getCurrentPlayer().getNumberOfArmies() == 0) {
      changeToNextPhase(gameMap);
    } else {
      display(
          gameMap.getCurrentPlayer().getPlayerName()
              + " has "
              + gameMap.getCurrentPlayer().getNumberOfArmies()
              + " army(s) to reinforce");
    }
    return result;
  }

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
      display("Fortified " + toCountry + " with " + armyToMove + " army(s) from " + fromCountry);
      changeToNextPhase(gameMap);
    } else {
      display("Player doesnt own the country or it does not exist in map");
    }
    return result;
  }

  public static boolean fortifyNone(GameMap gameMap, String command) {
    display("player chose not to fortify");
    changeToNextPhase(gameMap);
    return true;
  }

  public static boolean showMap(GameMap gameMap, String command) {
    display(gameMap.showMapByOwnership());
    return false;
  }

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

  public static void startPhaseLoop(GameMap gameMap) {
    CLI.getInstance().setCurrentContext(Context.GAME_REINFORCE);
    gameMap
        .getCurrentPlayer()
        .setNumberOfArmies(gameMap.getCurrentPlayer().calculateReinforcements(gameMap));
    display(gameMap.getCurrentPlayer().getPlayerName() + "'s turn:");
    display("[Reinforce]");
    display(
        gameMap.getCurrentPlayer().getPlayerName()
            + " has "
            + gameMap.getCurrentPlayer().getNumberOfArmies()
            + " army(s) to reinforce");
  }

  public static boolean attackNone(GameMap gameMap, String s) {
    display("player chose not to attack");
    changeToNextPhase(gameMap);
    return true;
  }
}
