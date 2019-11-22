package utils;

import models.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This singleton utility class handles all the text commands for the game.
 *
 * @author Siddhant
 * @version 1.0
 */
public class CLI {

  /** The scanner object for reading input from the console. */
  public static Scanner input;
  /** A Map of all the valid commands for a specific Context. */
  static Map<Context, ArrayList<String>> validCommands = new HashMap<>();
  /** The singleton instance of the CLI class. */
  private static CLI cli = null;

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
}
