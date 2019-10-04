package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This utility class handles all the text commands for the game.
 * 
 * @author Siddhant Bansal
 *
 */
public class CLI {

  public static enum Context {
    MAIN_MENU, GAME_SETUP, GAME_REINFORCE, GAME_ATTACK, GAME_FORTIFY, EDITOR
  }

  Map<Context, ArrayList<String>> _validCommands = new HashMap<>();

  private static CLI _cli = null;
  public static Scanner input;
  public Context currentContext;

  // list of commands
  // EDITOR
  // editcontinent, editcountry, editneighbour, showmap

  private CLI() {
    input = new Scanner(System.in);

    // Main Menu commands
    ArrayList<String> mainMenuCommands = new ArrayList<String>();
    mainMenuCommands.addAll(Arrays.asList("loadmap", "editmap"));
    _validCommands.put(Context.MAIN_MENU, mainMenuCommands);

    // Editor commands
    ArrayList<String> editorCommands = new ArrayList<String>();
    editorCommands.addAll(Arrays.asList("savemap", "validatemap", "editcountry", "editcontinent",
        "editneighbor", "showmap"));
    _validCommands.put(Context.EDITOR, editorCommands);

    // Setup commands
    ArrayList<String> setupCommands = new ArrayList<String>();
    setupCommands.addAll(Arrays.asList("placearmy", "placeall", "gameplayer", "populatecountry"));
    _validCommands.put(Context.GAME_SETUP, setupCommands);

    // Reinforcement commands
    ArrayList<String> reinforcementCommands = new ArrayList<String>();
    reinforcementCommands.addAll(Arrays.asList("reinforce"));
    _validCommands.put(Context.GAME_REINFORCE, reinforcementCommands);

    // Attack commands

    // Fortification commands
    ArrayList<String> fortificationCommands = new ArrayList<String>();
    fortificationCommands.addAll(Arrays.asList("fortify"));
    _validCommands.put(Context.GAME_FORTIFY, fortificationCommands);

  }

  public static CLI getInstance() {
    if (_cli == null) {
      _cli = new CLI();
    }
    return _cli;
  }

  public String process() {
    return "";
  }

  public boolean validate(String command) {
    if (_validCommands.get(currentContext).contains(command.split(" ")[0]))
      return true;
    else
      return false;
  }

  public Context getCurrentContext() {
    return currentContext;
  }

  public void setCurrentContext(Context _currentContext) {
    this.currentContext = _currentContext;
  }

}
