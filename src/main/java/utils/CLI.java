package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This singleton utility class handles all the text commands for the game.
 * 
 * @author Siddhant Bansal
 *
 */
public class CLI {

  /**
   * This enum maintains the context the commands are being called in, i.e. if the command is valid
   * for the phase we're currently in.
   * 
   * @author Siddhant Bansal
   *
   */
  public static enum Context {
    MAIN_MENU, GAME_SETUP, GAME_REINFORCE, GAME_ATTACK, GAME_FORTIFY, EDITOR
  }

  /**
   * A Map of all the valid commands for a specific Context.
   */
  Map<Context, ArrayList<String>> validCommands = new HashMap<>();

  /**
   * The singleton instance of the CLI class.
   */
  private static CLI cli = null;

  /**
   * The scanner object for reading input from the console.
   */
  public static Scanner input;

  /**
   * The current context for the game.
   */
  public Context currentContext;

  /**
   * The constructor for the CLI class, initiates all the command lists and puts them in the
   * validCommands map.
   */
  private CLI() {
    input = new Scanner(System.in);

    // Main Menu commands
    ArrayList<String> mainMenuCommands = new ArrayList<String>();
    mainMenuCommands.addAll(Arrays.asList("loadmap", "editmap"));
    validCommands.put(Context.MAIN_MENU, mainMenuCommands);

    // Editor commands
    ArrayList<String> editorCommands = new ArrayList<String>();
    editorCommands.addAll(Arrays.asList("savemap", "validatemap", "editcountry", "editcontinent",
        "editneighbor", "showmap"));
    validCommands.put(Context.EDITOR, editorCommands);

    // Setup commands
    ArrayList<String> setupCommands = new ArrayList<String>();
    setupCommands.addAll(Arrays.asList("placearmy", "placeall", "gameplayer", "populatecountry"));
    validCommands.put(Context.GAME_SETUP, setupCommands);

    // Reinforcement commands
    ArrayList<String> reinforcementCommands = new ArrayList<String>();
    reinforcementCommands.addAll(Arrays.asList("reinforce"));
    validCommands.put(Context.GAME_REINFORCE, reinforcementCommands);

    // Attack commands

    // Fortification commands
    ArrayList<String> fortificationCommands = new ArrayList<String>();
    fortificationCommands.addAll(Arrays.asList("fortify"));
    validCommands.put(Context.GAME_FORTIFY, fortificationCommands);

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
   * Validates the command with regards to the current context.
   * 
   * @param command The command as a String.
   * @return A boolean based on whether the command is valid or not (in the current phase).
   */
  public boolean validate(String command) {
    if (validCommands.get(currentContext).contains(command.split(" ")[0]))
      return true;
    else
      return false;
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
