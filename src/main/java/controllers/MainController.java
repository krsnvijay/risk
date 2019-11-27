package controllers;

import models.Command;
import models.Context;
import models.GameMap;
import utils.GamePersistenceHandler;
import utils.MapAdaptor;

import java.io.FileNotFoundException;
import java.util.Arrays;

import static views.ConsoleView.display;

/**
 * Controller for Main Menu Context
 *
 * @author Siddharth
 * @version 1.0
 */
public class MainController {

  /**
   * Process editmap command from the user loads a map and changes the context to allow the user to
   * execute map editor commands. Creates a map file if it doesn't exist.
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true if loaded map successfully
   */
  public static boolean processEditMapCommand(GameMap gameMap, String command) {
    String fileLocation = command.split(" ", 2)[1];
    boolean result = false;
    boolean newFile = false;
    GameMap newGameMap = null;
    try {

      MapAdaptor mapAdaptor = new MapAdaptor();
      newGameMap = mapAdaptor.autoLoadMap(fileLocation);
      result = true;
    } catch (FileNotFoundException e) {
      newGameMap = GameMap.getGameMap();
      result = true;
      newFile = true;
    } catch (Exception e) {
      display(e.getMessage(), false);
    }

    if (result) {
      if (newFile) {
        display("File not found! Empty map object provided to edit.", false);
      } else {
        display("Map loaded successfully", false);
      }
      GameMap.modifyInstance(newGameMap);
      GameMap.getGameMap().setCurrentContext(Context.MAP_EDITOR);
    }
    return result;
  }

  /**
   * Processes loadmap command from the cli Loads a map file and changes context to start the game
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true if loaded map successfully
   */
  public static boolean processLoadMapCommand(GameMap gameMap, String command) {
    String fileLocation = command.split(" ", 2)[1];
    GameMap newGameMap = null;
    boolean result = false;
    try {
      MapAdaptor mapAdaptor = new MapAdaptor();
      newGameMap = mapAdaptor.autoLoadMap(fileLocation);
      result = true;
    } catch (Exception e) {
      display(e.getMessage(), false);
    }
    if (result) {
      display("Map loaded successfully", false);
      if (gameMap.getPlayersList().size() != 0) {
        newGameMap.setPlayersList(gameMap.getPlayersList());
      }
      GameMap.modifyInstance(newGameMap);
      GameMap.getGameMap().setCurrentContext(Context.GAME_SETUP);
    } else {
      display("Invalid map", false);
    }
    return result;
  }

  /**
   * Process gameplayer commands from the cli Adds or removes players in the game state
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return boolean to indicate status
   */
  public static boolean processGamePlayerCommand(GameMap gameMap, String command) {
    String[] commandSplit = command.split(" -");
    boolean result = false;
    for (String subCommand : Arrays.copyOfRange(commandSplit, 1, commandSplit.length)) {
      String[] subCommandSplit = subCommand.split(" ");
      String subCommandType = subCommandSplit[0];
      String playerName = subCommandSplit[1];
      if (subCommandType.equals("add")) {
        String strategy = subCommandSplit[2];
        result = gameMap.addGamePlayer(playerName, strategy);
        if (result) {
          display(String.format("Added %s", playerName), false);
        } else {
          display(String.format("%s already exists", playerName), false);
        }
      } else {
        result = gameMap.removeGamePlayer(playerName);
        if (result) {
          display(String.format("Removed %s", playerName), false);
        } else {
          display(String.format("%s does not exist", playerName), false);
        }
      }
    }
    return result;
  }

  /**
   * Processes exitgame command from the cli Exits the game, terminates program completely
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return status
   */
  public static boolean processExitGameCommand(GameMap gameMap, String command) {
    display("Closing Risk Game", false);
    System.exit(0);
    return true;
  }

  /**
   * Processes help command from the cli Prints available commands according to the current context
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true to indicate status
   */
  public static boolean processGameHelpCommand(GameMap gameMap, String command) {
    Command[] validCommands = gameMap.getCurrentContext().getValidCommands();
    System.out.println("Available Commands:");
    for (Command validCommand : validCommands) {
      display("\t" + validCommand.getUsage(), false);
    }
    return true;
  }

  /**
   * Processes loadgame command from the cli
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return true to indicate status
   */
  public static boolean processLoadGameCommand(GameMap gameMap, String command) {
    String fileLocation = command.split(" ", 2)[1];
    try {
      return GamePersistenceHandler.loadState(fileLocation);
    } catch (Exception e) {
      display("Invalid gamestate or file does not exist", true);
      display(e.getMessage(), false);
      return false;
    }
  }
}
