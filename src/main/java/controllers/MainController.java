package controllers;

import static views.ConsoleView.display;

import java.util.ArrayList;
import java.util.Arrays;
import models.Command;
import models.Context;
import models.GameMap;
import models.Player;
import utils.CLI;
import utils.MapParser;

public class MainController {

  public static boolean editMap(GameMap gameMap, String command) {
    String fileLocation = command.split(" ", 2)[1];
    boolean result = false;
    GameMap gameMap1 = null;
    try {
      gameMap1 = MapParser.loadMap(fileLocation);
      result = true;
    } catch (Exception e) {
      display(e.getMessage());
    }

    if (result) {
      CLI.getInstance().setGameMap(gameMap1);
      CLI.getInstance().setCurrentContext(Context.MAP_EDITOR);
    }
    return result;
  }

  public static boolean loadMap(GameMap gameMap, String command) {
    String fileLocation = command.split(" ", 2)[1];
    GameMap gameMap1 = null;
    boolean result = false;
    try {
      gameMap1 = MapParser.loadMap(fileLocation);
      result = true;
    } catch (Exception e) {
      display(e.getMessage());
    }
    if (result) {
      display("Map loaded successfully");
      CLI.getInstance().setGameMap(gameMap1);
      CLI.getInstance().setCurrentContext(Context.GAME_SETUP);
    } else {
      display("Invalid map");
    }
    return result;
  }

  public static boolean gamePlayer(GameMap gameMap, String command) {
    String[] commandSplit = command.split(" -");
    boolean result = false;
    for (String subCommand : Arrays.copyOfRange(commandSplit, 1, commandSplit.length)) {
      String[] subCommandSplit = subCommand.split(" ");
      String subCommandType = subCommandSplit[0];
      String playerName = subCommandSplit[1];
      ArrayList<Player> playersList = gameMap.getPlayersList();
      if (subCommandType.equals("add")) {
        result = gameMap.addGamePlayer(playerName);
        if (result) {
          display("Added " + playerName);
        } else {
          display(playerName + " already exists");
          break;
        }
      } else {
        result = gameMap.removeGamePlayer(playerName);
        if (result) {
          display("Removed " + playerName);
        } else {
          display(playerName + " does not exist");
          break;
        }
      }
    }
    return result;
  }

  public static boolean exitGame(GameMap gameMap, String s) {
    display("Closing Risk Game");
    System.exit(0);
    return true;
  }

  public static boolean gameHelp(GameMap gameMap, String s) {
    Command[] validCommands = CLI.getInstance().getCurrentContext().getValidCommands();
    System.out.println("Available Commands:");
    for (Command command : validCommands) {
      display(command.getUsage());
    }
    return true;
  }
}
