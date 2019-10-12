package utils;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
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
    Arrays.stream(opCmds)
        .forEach(
            opcmd -> {
              String[] splitOpCmd = opcmd.split(" ");
              String continent = splitOpCmd[1];
              Map<String, Continent> copyOfContinents = map.getContinents();
              Map<String, Country> copyOfCountries = map.getCountries();
              Map<String, Set<String>> copyOfBorders = map.getBorders();
              if (splitOpCmd[0].equals("add")) {
                String continentControlValue = splitOpCmd[2];
                Continent toInsert =
                    new Continent(continent, Integer.parseInt(continentControlValue));
                copyOfContinents.put(continent, toInsert);
                map.setContinents(copyOfContinents);
              } else if (splitOpCmd[0].equals("remove")) {
                // perform remove continent
                ArrayList<String> countryBlackList = new ArrayList<>();
                map.setContinents(
                    copyOfContinents.entrySet().stream()
                        .filter(continentLoc -> !continentLoc.getKey().equals(continent))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
                // update countries list
                map.setCountries(
                    copyOfCountries.entrySet().stream()
                        .filter(
                            country -> {
                              if (country.getValue().getContinent().equals(continent)) {
                                countryBlackList.add(country.getKey());
                                return false;
                              }
                              return true;
                            })
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
                // update borders list
                map.setBorders(
                    copyOfBorders.entrySet().stream()
                        .filter(
                            border -> {
                              if (countryBlackList.contains(border.getKey())) {
                                return false;
                              }
                              border.setValue(
                                  border.getValue().stream()
                                      .filter(neighbor -> !countryBlackList.contains(neighbor))
                                      .collect(toSet()));
                              return true;
                            })
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
              }
            });
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
    Arrays.asList(opCmds)
        .forEach(
            opcmd -> {
              Map<String, Country> copyOfCountries = map.getCountries();
              Map<String, Set<String>> copyOfBorders = map.getBorders();
              String[] splitOpCmd = opcmd.split(" ");
              String country = splitOpCmd[1];
              if (splitOpCmd[0].equals("add")) {
                String continentString = splitOpCmd[2];
                Country toInsert = new Country(country, continentString);
                copyOfCountries.put(country, toInsert);
                map.setCountries(copyOfCountries);
                // perform add country
              } else if (splitOpCmd[0].equals("remove")) {
                // perform remove country
                // update country list
                copyOfCountries.remove(country);
                map.setCountries(copyOfCountries);
                // update border list
                map.setBorders(
                    copyOfBorders.entrySet().stream()
                        .filter(
                            borderLoc -> {
                              if (borderLoc.getKey().equals(country)) {
                                return false;
                              }
                              borderLoc.setValue(
                                  borderLoc.getValue().stream()
                                      .filter(neighbor -> !neighbor.equals(country))
                                      .collect(toSet()));
                              return true;
                            })
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
              }
            });
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
    Arrays.asList(opCmds)
        .forEach(
            opcmd -> {
              Map<String, Set<String>> copyOfBorders = map.getBorders();
              Map<String, Country> copyOfCountries = map.getCountries();
              String[] splitOpCmd = opcmd.split(" ");
              String country = splitOpCmd[1];
              String neighborCountry = splitOpCmd[2];
              if (splitOpCmd[0].equals("add")) {
                // perform add country neighborCountry
                if (country.equals(neighborCountry)) return;
                if (!copyOfBorders.containsKey(country)
                    && copyOfCountries.containsKey(neighborCountry)) {
                  copyOfBorders.put(country, new HashSet<>());
                  map.setBorders(copyOfBorders);
                }
                copyOfBorders.get(country).add(neighborCountry);
                map.setBorders(copyOfBorders);
              } else if (splitOpCmd[0].equals("remove")) {
                // perform remove country neighborCountry
                if (copyOfBorders.containsKey(country)) {
                  copyOfBorders.get(country).remove(neighborCountry);
                }
                map.setBorders(copyOfBorders);
              }
            });
    return true;
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
