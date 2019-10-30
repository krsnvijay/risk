package controllers;

import static views.ConsoleView.display;

import java.io.File;
import java.util.Arrays;
import models.Context;
import models.GameMap;
import utils.CLI;
import utils.EditMap;
import utils.MapParser;

/**
 * Controller for Editor Context
 */
public class EditorController {

  /**
   * Processes the edit continent command.
   *
   * @param gameMap The GameMap object to modify.
   * @param command The command received from cli
   * @return A boolean with success or failure for the command.
   */
  public static boolean editContinent(GameMap gameMap, String command) {
    boolean result = false;
    String[] commandSplit = command.split(" -");

    for (String subCommand : Arrays.copyOfRange(commandSplit, 1, commandSplit.length)) {
      String[] subCommandSplit = subCommand.split(" ");
      String commandType = subCommandSplit[0];
      String continentName = subCommandSplit[1];
      if (commandType.equals("add")) {
        int continentControlValue = Integer.parseInt(subCommandSplit[2]);
        result = gameMap.addContinent(continentName, continentControlValue);
        if (result) {
          display(String.format("Added continent: %s", continentName));
        }
      } else {
        result = gameMap.removeContinent(continentName);
        if (result) {
          display(String.format("Removed continent: %s", continentName));
        } else {
          display(String.format("The continent %s does not exist", continentName));
          break;
        }
      }
    }
    return result;
  }

  /**
   * Processes the edit country command.
   *
   * @param gameMap The GameMap object to modify
   * @param command The command received from cli
   * @return A boolean result for success or failure.
   */
  public static boolean editCountry(GameMap gameMap, String command) {
    boolean result = false;
    String[] commandSplit = command.split(" -");
    for (String subCommand : Arrays.copyOfRange(commandSplit, 1, commandSplit.length)) {
      String[] subCommandSplit = subCommand.split(" ");
      String commandType = subCommandSplit[0];
      String countryName = subCommandSplit[1];
      if (commandType.equals("add")) {
        String continentName = subCommandSplit[2];
        result = gameMap.addCountry(countryName, continentName);
        if (result) {
          display(String.format("Added country: %s to %s", countryName, continentName));
        } else {
          display(String.format("The continent %s does not exist", continentName));
          break;
        }
      } else {
        result = gameMap.removeCountry(countryName);
        if (result) {
          display(String.format("Removed country: %s", countryName));
        } else {
          display(String.format("The country %s does not exist", countryName));
          break;
        }
      }
    }
    return result;
  }

  /**
   * Processes the edit neighbour command.
   *
   * @param command The command options.
   * @param gameMap The GameMap object to modify.
   * @return A boolean with success or failure.
   */
  public static boolean editNeighbor(GameMap gameMap, String command) {
    boolean result = false;
    String[] commandSplit = command.split(" -");
    for (String subCommand : Arrays.copyOfRange(commandSplit, 1, commandSplit.length)) {
      String[] subCommandSplit = subCommand.split(" ");
      String commandType = subCommandSplit[0];
      String country1 = subCommandSplit[1];
      String country2 = subCommandSplit[2];
      if (country1.equals(country2)) {
        result = false;
        display(String.format("The countries %s and %s are the same", country1, country2));
        break;
      }
      if (commandType.equals("add")) {
        result = gameMap.addBorder(country1, country2);
        if (result) {
          display(String.format("Added border: %s - %s", country1, country2));
        } else {
          display(String.format("One of the countries %s, %s does not exist", country2, country1));
          break;
        }
      } else if (commandType.equals("remove")) {
        result = gameMap.removeBorder(country1, country2);
        if (result) {
          display("Removed border: " + country1 + " - " + country2);
        } else {
          display(String.format("One of the countries %s, %s does not exist", country2, country1));
          break;
        }
      }
    }
    return result;
  }

  /**
   * Processes validatemap from the cli Validates a map's connectivity and its subgraph connectivity
   *
   * @param gameMap contains game state
   * @param command cli command from the user
   * @return boolean to indicate map validity
   */
  public static boolean validateMap(GameMap gameMap, String command) {
    boolean result = EditMap.validateMap(gameMap);
    if (result) {
      display("Game map is valid");
    } else {
      display("Game map is invalid");
    }
    return result;
  }

  /**
   * Processes savemap from the cli Serializes the game state and saves it to a filelocation
   *
   * @param gameMap contains game state
   * @param command cli command from the user containing filelocation
   * @return boolean to indicate the status
   */
  public static boolean saveMap(GameMap gameMap, String command) {
    String fileLocation = command.split(" ", 2)[1];
    boolean result = false;
    boolean isMapValid = validateMap(gameMap, command);
    if (isMapValid) {
      result = MapParser.saveMap(gameMap, fileLocation);
      if (result) {
        display("Game map saved to " + fileLocation);
        CLI.getInstance().setCurrentContext(Context.MAIN_MENU);
      } else {
        display(fileLocation + " is invalid");
      }
    } else {
      display("Can't save an invalid map");
    }
    return result;
  }

  /**
   * Show map connectivity, continents, countries
   *
   * @param gameMap contains game state
   * @param command command from the cli
   * @return true indicating status
   */
  public static boolean showMap(GameMap gameMap, String command) {
    display(gameMap.toString());
    return true;
  }
}
