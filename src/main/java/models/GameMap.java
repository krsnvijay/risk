package models;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GameMap stores map data i.e borders, countries, files, continents
 *
 * @author Vijay
 */
public class GameMap {

  /** Contains the information in the [File] section. */
  private ArrayList<String> fileSectionData;

  /** Stores an adjacency list of all borders. */
  private Map<String, Set<String>> borders;

  /** Stores a map of all continents. */
  private Map<String, Continent> continents;

  /** Stores a map of all countries. */
  private Map<String, Country> countries;

  /** The name of the map file. */
  private String fileName;

  /**
   * This is the constructor for the GameMap class.
   *
   * @param fileSectionData Contains the information in the [File] section.
   * @param borders Stores an adjacency list of all borders.
   * @param continents Stores a map of all continents.
   * @param countries Stores a map of all countries.
   * @param fileName contains map name
   */
  public GameMap(
      ArrayList<String> fileSectionData,
      Map<String, Set<String>> borders,
      Map<String, Continent> continents,
      Map<String, Country> countries,
      String fileName) {
    super();
    this.fileSectionData = fileSectionData;
    this.borders = borders;
    this.continents = continents;
    this.countries = countries;
    this.fileName = fileName;
  }

  /**
   * Displays all the borders of the game map.
   *
   * @param border The adjacency list of each border
   * @return returns a pretty string of borders.
   */
  public static String showBorders(Map.Entry<String, Set<String>> border) {
    return String.format("%s %s", border.getKey(), String.join(" ", border.getValue()));
  }

  /** Displays the map grouped by continents. */
  public void showMapByContinents() {
    Map<String, List<Country>> groupedCountries =
        this.countries.values().stream().collect(groupingBy(Country::getContinent));
    String mapByContinents =
        groupedCountries.entrySet().stream()
            .map(
                entry -> {
                  String sectionHeader = entry.getKey();
                  String sectionData =
                      entry.getValue().stream()
                          .map(Country::getName)
                          .sorted()
                          .collect(joining("\n"));
                  return String.format("[%s]\n%s\n", sectionHeader, sectionData);
                })
            .collect(joining("\n"));
    System.out.println(mapByContinents);
  }

  /**
   * Get all countries in a continent
   *
   * @param continentName name of the continent
   * @return set of countrynames that are part of continent
   */
  public Set<String> getCountriesByContinent(String continentName) {
    return this.countries.values().stream()
        .filter(c -> c.getContinent().equals(continentName))
        .map(Country::getName)
        .collect(Collectors.toSet());
  }

  /**
   * Add a continent to the gameMap
   *
   * @param continentName continent to add
   * @param value control value of continent
   */
  public void addContinent(String continentName, int value) {
    Continent continent = new Continent(continentName, value);
    this.continents.put(continentName, continent);
    System.out.println("Added continent: " + continentName);
  }

  /**
   * Removes a continent and all its countries from the gameMap
   *
   * @param continentName continent to remove
   */
  public void removeContinent(String continentName) {
    if (!this.continents.containsKey(continentName)) {
      System.out.println("Error: The continent " + continentName + " does not exist");
      return;
    }
    getCountriesByContinent(continentName).forEach(this::removeCountry);
    this.continents.remove(continentName);
    System.out.println("Removed continent: " + continentName);
  }

  /**
   * Adds a country to the game map
   *
   * @param countryName country to add
   * @param continentName continent the country belongs to
   */
  public void addCountry(String countryName, String continentName) {
    if (!this.continents.containsKey(continentName)) {
      System.out.println("Error: The continent " + continentName + " does not exist");
      return;
    }
    Country country = new Country(countryName, continentName);
    this.countries.put(countryName, country);
    this.borders.put(countryName, new HashSet<>());
    System.out.println("Added country: " + countryName + " to " + continentName);
  }

  /**
   * Removes a country from the game map
   *
   * @param countryName country to remove
   */
  public void removeCountry(String countryName) {
    if (!this.countries.containsKey(countryName)) {
      System.out.println("Error: The country " + countryName + " does not exist");
      return;
    }
    removeCountryBorders(countryName);
    this.countries.remove(countryName);
    System.out.println("Removed country: " + countryName);
  }

  /**
   * Adds a border between two countries making them neighbors
   *
   * @param country1 neighboring country 1
   * @param country2 neighboring country 2
   */
  public void addBorder(String country1, String country2) {
    if (country1.equals(country2)) {
      System.out.println("Error: The countries " + country1 + " and " + country2 + " are the same");
      return;
    }
    if (!this.countries.containsKey(country1)) {
      System.out.println("Error: The country " + country1 + " does not exist");
      return;
    }
    if (!this.countries.containsKey(country2)) {
      System.out.println("Error: The country " + country2 + " does not exist");
      return;
    }
    this.borders.get(country1).add(country2);
    this.borders.get(country2).add(country1);
    System.out.println("Added border: " + country1 + " - " + country2);
  }

  /**
   * Removes a border between two countries
   *
   * @param country1 neighboring country 1
   * @param country2 neighboring country 2
   */
  public void removeBorder(String country1, String country2) {
    if (country1.equals(country2)) {
      System.out.println("Error: The countries" + country1 + " and " + country2 + " are the same");
      return;
    }
    if (!this.countries.containsKey(country1)) {
      System.out.println("Error: The country " + country1 + " does not exist");
      return;
    }
    if (!this.countries.containsKey(country2)) {
      System.out.println("Error: The country " + country2 + " does not exist");
      return;
    }
    this.borders.get(country1).remove(country2);
    this.borders.get(country2).remove(country1);
    System.out.println("Removed border: " + country1 + " - " + country2);
  }

  /**
   * Removes all borders that a country is part of
   *
   * @param countryName country whose borders are to be removed
   */
  public void removeCountryBorders(String countryName) {
    if (!this.borders.containsKey(countryName)) {
      System.out.println("Error: The country " + countryName + " does not exist");
      return;
    }
    Set<String> neighbors = this.borders.get(countryName);
    for (String neighbor : neighbors) {
      this.borders.get(neighbor).remove(countryName);
      System.out.println("Removed border: " + neighbor + " - " + countryName);
    }
    this.borders.remove(countryName);
  }

  /** Pretty prints the game map. */
  @Override
  public String toString() {
    return String.format(
        "[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n",
        this.continents.values().stream().map(Continent::toString).sorted().collect(joining("\n")),
        this.countries.values().stream().map(Country::toString).sorted().collect(joining("\n")),
        this.borders.entrySet().stream().map(GameMap::showBorders).sorted().collect(joining("\n")));
  }

  /**
   * This method returns all the borders.
   *
   * @return A Map of all borders in the game map
   */
  public Map<String, Set<String>> getBorders() {
    return borders;
  }

  /**
   * This sets the borders instance variable for the game map class.
   *
   * @param borders A Map, consisting of the country name and the adjacency of borders.
   */
  public void setBorders(Map<String, Set<String>> borders) {
    this.borders = borders;
  }

  /**
   * This method returns a Map object with all the continents.
   *
   * @return A Map object, mapping the name with the Continent object.
   */
  public Map<String, Continent> getContinents() {
    return continents;
  }

  /**
   * This method sets the continents object for the GameMap class.
   *
   * @param continents A Map object, mapping the name with the Continent object.
   */
  public void setContinents(Map<String, Continent> continents) {
    this.continents = continents;
  }

  /**
   * Returns a section parsed from the file.
   *
   * @return A list of raw lines.
   */
  public ArrayList<String> getFileSectionData() {
    return this.fileSectionData;
  }

  /**
   * Sets the fileSectionData variable for the GameMap class.
   *
   * @param fileSectionData An ArrayList with file sections as strings.
   */
  public void setFileSectionData(ArrayList<String> fileSectionData) {
    this.fileSectionData = fileSectionData;
  }

  /**
   * Returns the name of the file.
   *
   * @return the name of the file.
   */
  public String getFileName() {
    return this.fileName;
  }

  /**
   * Sets the name of the map file.
   *
   * @param fileName the name of the file.
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Returns a Map object of countries.
   *
   * @return a Map of countries.
   */
  public Map<String, Country> getCountries() {
    return countries;
  }

  /**
   * Sets the countries instance variable for the GameMap
   *
   * @param countries a Map object of country names and Country objects.
   */
  public void setCountries(Map<String, Country> countries) {
    this.countries = countries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GameMap gameMap = (GameMap) o;
    return fileSectionData.equals(gameMap.fileSectionData) &&
        borders.equals(gameMap.borders) &&
        continents.equals(gameMap.continents) &&
        countries.equals(gameMap.countries) &&
        fileName.equals(gameMap.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileSectionData, borders, continents, countries, fileName);
  }
}
