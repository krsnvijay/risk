package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameMap {
  private ArrayList<String> fileSectionData;
  private Map<String, Set<String>> borders;
  private Map<String, Continent> continents;
  private Map<String, Country> countries;
  private String fileName;

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

  public static Pair<String, Country> mapCountry(String countryLine,
      ArrayList<String> continentNames) {
    String[] splitCountryLine = countryLine.split(" ");
    String name = splitCountryLine[1];
    String continent = continentNames.get(Integer.parseInt(splitCountryLine[2]) - 1);
    int x = Integer.parseInt(splitCountryLine[3]);
    int y = Integer.parseInt(splitCountryLine[4]);
    return new Pair<>(name, new Country(name, continent, x, y));
  }

  public static Pair<String, Continent> mapContinent(String continentLine) {
    String[] splitContinentLine = continentLine.split(" ");
    String name = splitContinentLine[0];
    int value = Integer.parseInt(splitContinentLine[1]);
    String color = splitContinentLine[2];
    return new Pair<>(name, new Continent(color, name, value));
  }

  public static Pair<String, Set<String>> mapBorders(String borderLine,
      ArrayList<String> countryNames) {
    String[] splitBorderLine = borderLine.split(" ");
    String key = countryNames.get(Integer.parseInt(splitBorderLine[0]) - 1);
    Set<String> value = Arrays.stream(splitBorderLine, 1, splitBorderLine.length)
        .map(str -> countryNames.get(Integer.parseInt(str) - 1)).collect(Collectors.toSet());
    return new Pair<>(key, value);
  }

  public static String showBorders(Map.Entry<String, Set<String>> border) {
    return String.format("%s %s", border.getKey(), String.join(" ", border.getValue()));
  }

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


  @Override
  public String toString() {
    return String.format("[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n",
        this.continents.values().stream().map(Continent::toString)
            .collect(Collectors.joining("\n")),
        this.countries.values().stream().map(Country::toString).collect(Collectors.joining("\n")),
        this.borders.entrySet().stream().map(GameMap::showBorders)
            .collect(Collectors.joining("\n")));
  }

  public Map<String, Set<String>> getBorders() {
    return borders;
  }

  public void setBorders(Map<String, Set<String>> borders) {
    this.borders = borders;
  }

  public Map<String, Continent> getContinents() {
    return continents;
  }

  public void setContinents(Map<String, Continent> continents) {
    this.continents = continents;
  }

  public ArrayList<String> getFileSectionData() {
    return this.fileSectionData;
  }

  public void setFileSectionData(ArrayList<String> fileSectionData) {
    this.fileSectionData = fileSectionData;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public Map<String, Country> getCountries() {
    return countries;
  }

  public void setCountries(Map<String, Country> countries) {
    this.countries = countries;
  }
}
