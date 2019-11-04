package utils;

import models.Continent;
import models.Country;
import models.GameMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;

/**
 * The Edit Map utility processes the editing commands.
 *
 * @author Warren White
 */
public class EditMap extends MapParser {

  /** The constructor for EditMap. Calls the MapParser constructor. */
  public EditMap() {
    super();
  }

  /**
   * Performs a DFS on every continent, works using recursion.
   *
   * @param map A List of all countries.
   * @param visited A HashMap of visited countries.
   * @param start The Country to start from.
   * @param completeMap The entire GameMap to look through.
   * @return visited countries of a continent
   */
  private static HashSet<Country> DFSUtilContinents(
      List<Country> map, HashSet<Country> visited, Country start, GameMap completeMap) {
    Map<String, Set<String>> copyOfBorders = completeMap.getBorders();
    Map<String, Country> copyOfCountries = completeMap.getCountries();
    visited.add(start);
    for (String neighbor : copyOfBorders.get(start.getName())) {
      if (map.contains(copyOfCountries.get(neighbor))
          && !visited.contains(copyOfCountries.get(neighbor))) {
        Country neighborCountry = copyOfCountries.get(neighbor);
        DFSUtilContinents(map, visited, neighborCountry, completeMap);
      }
    }
    return visited;
  }

  /**
   * Performs a DFS on the whole game map, works using recursion.
   *
   * @param visited A HashMap of visited countries.
   * @param start The name of the Country to start with.
   * @param completeMap The entire GameMap to look through.
   * @return visited countries of the whole map
   */
  private static HashSet<String> DFSUtilWholeMap(
      HashSet<String> visited, String start, GameMap completeMap) {
    visited.add(start);
    for (String neighbor : completeMap.getBorders().get(start)) {
      if (!visited.contains(neighbor)) {
        DFSUtilWholeMap(visited, neighbor, completeMap);
      }
    }
    return visited;
  }

  /**
   * Processes the edit continent command.
   *
   * @param opCmds The options entered in the command.
   * @param map The GameMap object to modify.
   * @return A boolean with success or failure for the command.
   */
  public boolean editContinent(String[] opCmds, GameMap map) {
    for (String opCmd : opCmds) {
      String[] splitOpCmd = opCmd.split(" ");
      if (splitOpCmd.length < 2 || splitOpCmd.length > 3) {
        System.out.println(
            "Invalid syntax: usage editcontinent -add <continentName> <controlValue> -remove <continentName>");
        continue;
      }
      String commandType = splitOpCmd[0];
      String continentName = splitOpCmd[1];
      if (commandType.equals("add")) {
        int continentControlValue = Integer.parseInt(splitOpCmd[2]);
        map.addContinent(continentName, continentControlValue);
      } else if (commandType.equals("remove")) {
        map.removeContinent(continentName);
      }
    }
    return true;
  }

  /**
   * Processes the edit country command.
   *
   * @param opCmds The command options
   * @param map The GameMap object to modify
   * @return A boolean result for success or failure.
   */
  public boolean editCountry(String[] opCmds, GameMap map) {
    for (String opCmd : opCmds) {
      String[] splitOpCmd = opCmd.split(" ");
      if (splitOpCmd.length < 2 || splitOpCmd.length > 3) {
        System.out.println(
            "Invalid syntax: usage editcountry -add <countryName> <continentName> -remove <countryName>");
        continue;
      }
      String commandType = splitOpCmd[0];
      String countryName = splitOpCmd[1];
      if (commandType.equals("add")) {
        String continentString = splitOpCmd[2];
        map.addCountry(countryName, continentString);
      } else if (commandType.equals("remove")) {
        map.removeCountry(countryName);
      }
    }
    return true;
  }

  /**
   * Processes the edit neighbour command.
   *
   * @param opCmds The command options.
   * @param map The GameMap object to modify.
   * @return A boolean with success or failure.
   */
  public boolean editNeighbor(String[] opCmds, GameMap map) {
    for (String opCmd : opCmds) {
      String[] splitOpCmd = opCmd.split(" ");
      if (splitOpCmd.length != 3) {
        System.out.println(
            "Invalid syntax: usage editneighbor -add <country1> <country2> -remove <country1> <country2>");
        continue;
      }
      String commandType = splitOpCmd[0];
      String country1 = splitOpCmd[1];
      String country2 = splitOpCmd[2];
      if (commandType.equals("add")) {
        map.addBorder(country1, country2);
      } else if (commandType.equals("remove")) {
        map.removeBorder(country1, country2);
      }
    }
    return true;
  }

  /**
   * Checks whether the current game map is a valid map or not.
   *
   * @param map The entire GameMap
   * @return A boolean with success or failure.
   */
  public static boolean validateMap(GameMap map) {
    Map<String, Set<String>> copyOfBorders = map.getBorders();
    Map<String, Country> copyOfCountries = map.getCountries();
    Map<String, Continent> copyOfContinents = map.getContinents();
    Map<String, List<Country>> groupedByCountries =
        copyOfCountries.values().stream().collect(groupingBy(Country::getContinent));
    // PRELIMINARY CHECKS
    if (copyOfContinents.keySet().size() != groupedByCountries.keySet().size())
      return false; // CASE - No countries in continent OR countries in Continent that doesn't exist
    if (copyOfBorders.keySet().size() != copyOfCountries.keySet().size())
      return false; // CASE - No borderList entry for country OR borderList entry without
    // countryList entry
    for (String country : copyOfBorders.keySet()) {
      if (copyOfBorders.get(country).size() == 0) return false; // CASE - Country has no borders
    }
    // CHECK CONNECTEDNESS OF SUBGRAPHS & WHOLE GRAPH
    // RUN DFS ON CONTINENTS
    if (!DFSCheckOnContinent(map)) {
      return false;
    }
    // RUN DFS ON WHOLE MAP
    HashSet<String> visited = new HashSet<>();
    copyOfCountries.keySet().stream()
        .findFirst()
        .ifPresent(startCountry -> DFSUtilWholeMap(visited, startCountry, map));
    return visited.size() == copyOfBorders.keySet().size();
  }

  /**
   * Performs DFS check on a continent to check its connectivity
   *
   * @param map game state
   * @return boolean to indicate status
   */
  public static boolean DFSCheckOnContinent(GameMap map) {
    Map<String, List<Country>> groupedByCountries =
        map.getCountries().values().stream().collect(groupingBy(Country::getContinent));
    for (String continent : groupedByCountries.keySet()) {
      List<Country> countriesInContinent = groupedByCountries.get(continent);
      HashSet<Country> visited = new HashSet<>();
      DFSUtilContinents(countriesInContinent, visited, countriesInContinent.get(0), map);
      if (visited.size() != countriesInContinent.size()) {
        return false;
      }
    }
    return true;
  }
}
