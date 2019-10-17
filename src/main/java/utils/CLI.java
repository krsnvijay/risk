package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import models.Context;
import models.GameMap;

/**
 * This singleton utility class handles all the text commands for the game.
 *
 * @author Siddhant Bansal
 */
public class CLI {

  /** The scanner object for reading input from the console. */
  public static Scanner input;

  /** The singleton instance of the CLI class. */
  private static CLI cli = null;

  /** The current context for the game. */
  public Context currentContext;
  /**
   * The game state
   */
  public static GameMap gameMap = null;

  /** A Map of all the valid commands for a specific Context. */
  static Map<Context, ArrayList<String>> validCommands = new HashMap<>();

  /**
   * The constructor for the CLI class, initiates all the command lists and puts them in the
   * validCommands map.
   */
  private CLI() {
    input = new Scanner(System.in);
  }

  /**
   * A method to get the existing instance of CLI, or creating one if it doesn't exist.
   *
   * @return The instance of the CLI class.
   */
  public static CLI getInstance() {
    if (cli == null) {
      cli = new CLI();
    }
    return cli;
  }

  /**
   * A method to get the existing instance of gameMap, or creating one if it doesn't exist.
   *
   * @return The instance of the gameMap.
   */
  public static GameMap getGameMap() {
    if (gameMap == null) {
      gameMap = new GameMap();
    }
    return gameMap;
  }

  /**
   * Setter for gamemap instance
   *
   * @param gameMap contains game state
   */
  public void setGameMap(GameMap gameMap) {
    CLI.gameMap = gameMap;
  }

  /**
   * Returns the current context.
   *
   * @return The current Context for the game.
   */
  public Context getCurrentContext() {
    return currentContext;
  }

  /**
   * Sets the current context.
   *
   * @param currentContext A Context object to set in the class.
   */
  public void setCurrentContext(Context currentContext) {
    this.currentContext = currentContext;
  }
}
