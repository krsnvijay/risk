package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Vijay
 *
 */
public class GameMap {
  /**
   * Contains the information in the [File] section.
   */
  private ArrayList<String> fileSectionData;

  /**
   * Stores an adjacency list of all borders.
   */
  private Map<String, Set<String>> borders;

  /**
   * Stores an adjacency list of all continents.
   */
  private Map<String, Continent> continents;

  /**
   * Stores an adjacency list of all countries.
   */
  private Map<String, Country> countries;

  /**
   * The name of the map file.
   */
  private String fileName;

  /**
   * This is the constructor for the GameMap class. It takes a hashmap with the file data and parses
   * the different sections using streams.
   * 
   * After that, the required sections are stored in the instance variables in the class.
   * 
   * @param fileName the name of the file.
   * @param mapData the entire gamemap.
   */
  public GameMap(String fileName, HashMap<String, ArrayList<String>> mapData) {
    this.fileName = fileName;
    this.fileSectionData = mapData.get("[files]");
    ArrayList<String> continentsInOrder = mapData.get("[continents]").stream()
        .map(line -> line.split(" ")[0]).collect(Collectors.toCollection(ArrayList::new));
    ArrayList<String> countriesInOrder = mapData.get("[countries]").stream()
        .map(line -> line.split(" ")[1]).collect(Collectors.toCollection(ArrayList::new));
    this.continents = mapData.get("[continents]").stream().map(GameMap::mapContinent)
        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    this.countries =
        mapData.get("[countries]").stream().map(line -> GameMap.mapCountry(line, continentsInOrder))
            .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    this.borders =
        mapData.get("[borders]").stream().map(line -> GameMap.mapBorders(line, countriesInOrder))
            .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
  }

  /**
   * This maps the country from the file into a Country object.
   * 
   * @param countryLine the raw line from the file.
   * @param continentNames the names of all continents
   * @return A Pair object, key is the name and the value is the Country object
   */
  public static Pair<String, Country> mapCountry(String countryLine,
      ArrayList<String> continentNames) {
    String[] splitCountryLine = countryLine.split(" ");
    String name = splitCountryLine[1];
    String continent = continentNames.get(Integer.parseInt(splitCountryLine[2]) - 1);
    int x = Integer.parseInt(splitCountryLine[3]);
    int y = Integer.parseInt(splitCountryLine[4]);
    return new Pair<>(name, new Country(name, continent, x, y));
  }

  /**
   * This maps the continent from the file into a Continent object.
   * 
   * @param continentLine the raw line from the file.
   * @return A Pair object, key is the name and the value is the Continent object
   */
  public static Pair<String, Continent> mapContinent(String continentLine) {
    String[] splitContinentLine = continentLine.split(" ");
    String name = splitContinentLine[0];
    int value = Integer.parseInt(splitContinentLine[1]);
    String color = splitContinentLine[2];
    return new Pair<>(name, new Continent(color, name, value));
  }

  /**
   * This maps the borders from the file into a Border object.
   * 
   * @param borderLine the raw line from the file.
   * @param countryNames A list of country names.
   * @return A Pair object, key is the name and the value is a Set of borders.
   */
  public static Pair<String, Set<String>> mapBorders(String borderLine,
      ArrayList<String> countryNames) {
    String[] splitBorderLine = borderLine.split(" ");
    String key = countryNames.get(Integer.parseInt(splitBorderLine[0]) - 1);
    Set<String> value = Arrays.stream(splitBorderLine, 1, splitBorderLine.length)
        .map(str -> countryNames.get(Integer.parseInt(str) - 1)).collect(Collectors.toSet());
    return new Pair<>(key, value);
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

  /**
   * Displays the map grouped by continents.
   */
  public void showMapByContinents() {
    Map<String, List<Country>> groupedCountries =
        this.countries.values().stream().collect(Collectors.groupingBy(Country::getContinent));
    String mapByContinents = groupedCountries.entrySet().stream().map(entry -> {
      String sectionHeader = entry.getKey();
      String sectionData = entry.getValue().stream().map(Country::getName).sorted()
          .collect(Collectors.joining("\n"));
      return String.format("[%s]\n%s\n", sectionHeader, sectionData);
    }).collect(Collectors.joining("\n"));
    System.out.println(mapByContinents);
  }


  /**
   * Pretty prints the game map.
   */
  @Override
  public String toString() {
    return String.format("[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n",
        this.continents.values().stream().map(Continent::toString)
            .collect(Collectors.joining("\n")),
        this.countries.values().stream().map(Country::toString).collect(Collectors.joining("\n")),
        this.borders.entrySet().stream().map(GameMap::showBorders)
            .collect(Collectors.joining("\n")));
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
   * @param continents
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
}
