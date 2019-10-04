package models;

import java.util.*;
import java.util.stream.Collectors;

public class GameMap {
  private ArrayList<String> fileSectionData;
  private Map<String, Set<String>> borders;
  private Map<String, Continent> continents;
  private Map<String, Country> countries;
  private String fileName;
  public GameMap(String fileName ,HashMap<String, ArrayList<String>> mapData) {
    this.fileName = fileName;
    this.fileSectionData = mapData.get("[files]");
    ArrayList<String> continentsInOrder =
            mapData.get("[continents]").stream()
                    .map(line -> line.split(" ")[0])
                    .collect(Collectors.toCollection(ArrayList::new));
    ArrayList<String> countriesInOrder =
        mapData.get("[countries]").stream()
                .map(line -> line.split(" ")[1])
            .collect(Collectors.toCollection(ArrayList::new));
    this.continents =
        mapData.get("[continents]").stream()
                .map(GameMap::mapContinent)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    this.countries =
            mapData.get("[countries]").stream()
                    .map(line -> GameMap.mapCountry(line, continentsInOrder))
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    this.borders =
        mapData.get("[borders]").stream()
                .map(line -> GameMap.mapBorders(line, countriesInOrder))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
  }

  public static Pair<String, Country> mapCountry(
          String countryLine, ArrayList<String> continentNames) {
    String[] splitCountryLine = countryLine.split(" ");
    String name = splitCountryLine[1];
    String continent = continentNames.get(Integer.parseInt(splitCountryLine[2]) - 1);
    return new Pair<>(name, new Country(name, continent));
  }

  public static Pair<String, Continent> mapContinent(String continentLine) {
    String[] splitContinentLine = continentLine.split(" ");
    String name = splitContinentLine[0];
    int value = Integer.parseInt(splitContinentLine[1]);
    String color = splitContinentLine[2];
    return new Pair<>(name, new Continent(color, name, value));
  }

  public static Pair<String, Set<String>> mapBorders(
          String borderLine, ArrayList<String> countryNames) {
    String[] splitBorderLine = borderLine.split(" ");
    String key = countryNames.get(Integer.parseInt(splitBorderLine[0]) - 1);
    Set<String> value =
            Arrays.stream(splitBorderLine, 1, splitBorderLine.length)
                    .map(str -> countryNames.get(Integer.parseInt(str) - 1))
            .collect(Collectors.toSet());
    return new Pair<>(key, value);
  }

  public static String showBorders(Map.Entry<String, Set<String>> border) {
    return String.format("%s %s", border.getKey(), String.join(" ", border.getValue()));
  }

  public void showMapByContinents() {
    Map<String, List<Country>> groupedCountries =
            this.countries.values().stream().collect(Collectors.groupingBy(Country::getContinent));
    String mapByContinents =
            groupedCountries.entrySet().stream()
                    .map(
                            entry -> {
                              String sectionHeader = entry.getKey();
                              String sectionData =
                                      entry.getValue().stream()
                                              .map(Country::getName)
                                              .sorted()
                                              .collect(Collectors.joining("\n"));
                              return String.format("[%s]\n%s\n", sectionHeader, sectionData);
                            })
                    .collect(Collectors.joining("\n"));
    System.out.println(mapByContinents);
  }

  public String serializeMap() {
    String files = this.fileSectionData.stream().collect(Collectors.joining("\n"));
    ArrayList<String> continentsOrder =
            this.continents.keySet().stream().sorted().collect(Collectors.toCollection(ArrayList::new));
    ArrayList<String> countriesOrder =
            this.countries.keySet().stream().sorted().collect(Collectors.toCollection(ArrayList::new));
    String continents =
            continentsOrder.stream()
                    .map(str -> this.getContinents().get(str))
                    .map(Continent::toString)
                    .collect(Collectors.joining("\n"));
    String countries =
            countriesOrder.stream()
                    .map(str -> this.getCountries().get(str))
                    .map(
                            country -> {
                              int countryId = countriesOrder.indexOf(country.getName()) + 1;
                              String countryName = country.getName();
                              int continentId = continentsOrder.indexOf(country.getContinent()) + 1;
                              return String.format("%d %s %d %d %d", countryId, countryName, continentId,country.getX(), country.getY());
                            })
                    .collect(Collectors.joining("\n"));

    String borders =
            countriesOrder.stream()
                    .map(
                            countryName -> {
                              int countryId = countriesOrder.indexOf(countryName) + 1;
                              String neighbors =
                                      this.getBorders().get(countryName).stream()
                                              .map(c -> countriesOrder.indexOf(c) + 1)
                                              .map(String::valueOf)
                                              .collect(Collectors.joining(" "));
                              return String.format("%d %s", countryId, neighbors);
                            })
                    .collect(Collectors.joining("\n"));
    return String.format(
            "%s\n\n[files]\n%s\n\n[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n", this.fileName,files,continents, countries, borders);
  }

  @Override
  public String toString() {
    return String.format(
        "[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n",
            this.continents.values().stream()
                    .map(Continent::toString)
                    .collect(Collectors.joining("\n")),
            this.countries.values().stream().map(Country::toString).collect(Collectors.joining("\n")),
            this.borders.entrySet().stream()
                    .map(GameMap::showBorders)
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

  public Map<String, Country> getCountries() {
    return countries;
  }

  public void setCountries(Map<String, Country> countries) {
    this.countries = countries;
  }
}
