package utils;

import static java.util.stream.Collectors.groupingBy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import models.Continent;
import models.Country;
import models.GameMap;

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
        addContinent(continentName, continentControlValue, map);
      } else if (commandType.equals("remove")) {
        removeContinent(continentName, map);
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
        addCountry(countryName, continentString, map);
      } else if (commandType.equals("remove")) {
        removeCountry(countryName, map);
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
        addBorder(country1, country2, map);
      } else if (commandType.equals("remove")) {
        removeBorder(country1, country2, map);
      }
    }
    return true;
  }

  /**
   * Add a continent to the gameMap
   *
   * @param continentName continent to add
   * @param value control value of continent
   * @param gameMap contains map data
   */
  public void addContinent(String continentName, int value, GameMap gameMap) {
    Continent continent = new Continent(continentName, value);
    gameMap.getContinents().put(continentName, continent);
    System.out.println("Added continent: " + continentName);
  }

  /**
   * Removes a continent and all its countries from the gameMap
   *
   * @param continentName continent to remove
   * @param gameMap contains map data
   */
  public void removeContinent(String continentName, GameMap gameMap) {
    if (!gameMap.getContinents().containsKey(continentName)) {
      System.out.println("Error: The continent " + continentName + " does not exist");
      return;
    }
    gameMap.getContinents().remove(continentName);
    gameMap
        .getCountriesByContinent(continentName)
        .forEach(country -> removeCountry(country, gameMap));
    System.out.println("Removed continent: " + continentName);
  }

  /**
   * Adds a country to the game map
   *
   * @param countryName country to add
   * @param continentName continent the country belongs to
   * @param gameMap contains map data
   */
  public void addCountry(String countryName, String continentName, GameMap gameMap) {
    if (!gameMap.getContinents().containsKey(continentName)) {
      System.out.println("Error: The continent " + continentName + " does not exist");
      return;
    }
    Country country = new Country(countryName, continentName);
    gameMap.getCountries().put(countryName, country);
    gameMap.getBorders().put(countryName, new HashSet<>());
    System.out.println("Added country: " + countryName + " to " + continentName);
  }

  /**
   * Removes a country from the game map
   *
   * @param countryName country to remove
   * @param gameMap contains map data
   */
  public void removeCountry(String countryName, GameMap gameMap) {
    if (!gameMap.getCountries().containsKey(countryName)) {
      System.out.println("Error: The country " + countryName + " does not exist");
      return;
    }
    gameMap.getCountries().remove(countryName);
    removeCountryBorders(countryName, gameMap);
    System.out.println("Removed country: " + countryName);
  }

  /**
   * Adds a border between two countries making them neighbors
   *
   * @param country1 neighboring country 1
   * @param country2 neighboring country 2
   * @param gameMap contains map data
   */
  public void addBorder(String country1, String country2, GameMap gameMap) {
    if (country1.equals(country2)) {
      System.out.println("Error: The countries " + country1 + " and " + country2 + " are the same");
      return;
    }
    Map<String, Set<String>> borders = gameMap.getBorders();
    borders.get(country1).add(country2);
    borders.get(country2).add(country1);
    System.out.println("Added border: " + country1 + " - " + country2);
  }

  /**
   * Removes a border between two countries
   *
   * @param country1 neighboring country 1
   * @param country2 neighboring country 2
   * @param gameMap contains map data
   */
  public void removeBorder(String country1, String country2, GameMap gameMap) {
    if (country1.equals(country2)) {
      System.out.println("Error: The countries" + country1 + " and " + country2 + " are the same");
      return;
    }
    if (!gameMap.getCountries().containsKey(country1)) {
      System.out.println("Error: The country " + country1 + " does not exist");
      return;
    }
    if (!gameMap.getCountries().containsKey(country2)) {
      System.out.println("Error: The country " + country2 + " does not exist");
      return;
    }
    Map<String, Set<String>> borders = gameMap.getBorders();
    borders.get(country1).remove(country2);
    borders.get(country2).remove(country1);
    System.out.println("Removed border: " + country1 + " - " + country2);
  }

  /**
   * Removes all borders that a country is part of
   *
   * @param countryName country whose borders are to be removed
   * @param gameMap contains map data
   */
  public void removeCountryBorders(String countryName, GameMap gameMap) {
    if (!gameMap.getBorders().containsKey(countryName)) {
      System.out.println("Error: The country " + countryName + " does not exist");
      return;
    }
    Map<String, Set<String>> borders = gameMap.getBorders();
    Set<String> neighbors = borders.get(countryName);
    for (String neighbor : neighbors) {
      borders.get(neighbor).remove(countryName);
      System.out.println("Removed border: " + neighbor + " - " + countryName);
    }
    borders.remove(countryName);
  }

  /**
   * Checks whether the current game map is a valid map or not.
   *
   * @param map The entire GameMap
   * @return A boolean with success or failure.
   */
  public boolean validateMap(GameMap map) {
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
    for (String continent : groupedByCountries.keySet()) {
      List<Country> countriesInContinent = groupedByCountries.get(continent);
      HashSet<Country> visited = new HashSet<>();
      DFSUtilContinents(countriesInContinent, visited, countriesInContinent.get(0), map);
      if (visited.size() != countriesInContinent.size()) {
        return false;
      }
    }
    // RUN DFS ON WHOLE MAP
    HashSet<String> visited = new HashSet<>();
    copyOfCountries.keySet().stream()
        .findFirst()
        .ifPresent(startCountry -> DFSUtilWholeMap(visited, startCountry, map));
    return visited.size() == copyOfBorders.keySet().size();
  }
}
