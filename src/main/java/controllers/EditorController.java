package controllers;

import static views.ConsoleView.display;

import java.util.Arrays;
import models.Context;
import models.GameMap;
import utils.CLI;
import utils.EditMap;
import utils.MapParser;

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
          display("Added continent: " + continentName);
        }
      } else {
        result = gameMap.removeContinent(continentName);
        if (result) {
          display("Removed continent: " + continentName);
        } else {
          display("The continent " + continentName + " does not exist");
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
          display("Added country: " + countryName + " to " + continentName);
        } else {
          display("The continent " + continentName + " does not exist");
          break;
        }
      } else {
        result = gameMap.removeCountry(countryName);
        if (result) {
          display("Removed country: " + countryName);
        } else {
          display("The country " + countryName + " does not exist");
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
        display("The countries " + country1 + " and " + country2 + " are the same");
        break;
      }
      if (commandType.equals("add")) {
        result = gameMap.addBorder(country1, country2);
        if (result) {
          display("Added border: " + country1 + " - " + country2);
        } else {
          display("One of the countries " + country2 + ", " + country1 + " does not exist");
          break;
        }
      } else if (commandType.equals("remove")) {
        result = gameMap.removeBorder(country1, country2);
        if (result) {
          display("Removed border: " + country1 + " - " + country2);
        } else {
          display("One of the countries " + country2 + ", " + country1 + " does not exist");
          break;
        }
      }
    }
    return result;
  }

  public static boolean validateMap(GameMap gameMap, String command) {
    boolean result = EditMap.validateMap(gameMap);
    if (result) {
      display("Game map is valid");
    } else {
      display("Game map is invalid");
    }
    return result;
  }

  public static boolean saveMap(GameMap gameMap, String command) {
    String fileLocation = command.split(" ", 2)[1];
    boolean result = MapParser.saveMap(gameMap, fileLocation);
    if (result) {
      display("Game map saved to " + fileLocation);
      CLI.getInstance().setCurrentContext(Context.MAIN_MENU);
    } else {
      display(fileLocation + " is invalid");
    }
    return result;
  }

  public static boolean showMap(GameMap gameMap, String command) {
    display(gameMap.toString());
    return true;
  }
}
