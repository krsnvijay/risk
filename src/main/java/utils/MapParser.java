package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import models.Continent;
import models.Country;
import models.GameMap;
import models.Pair;

/**
 * The Map Parser utility parses the whole map file from disk.
 * 
 * @author v_valla
 */
public class MapParser {

  /**
   * Loads the map file and parses it.
   * 
   * @param fileName the location of the map file to be parsed
   * @return GameMap the parsed GameMap Object
   * @throws IOException when file location/contents are invalid
   * @throws Exception when map file doesn't contain all the required sections
   */
  public static GameMap loadMap(String fileName) throws IOException, Exception {
    ArrayList<String> fileSectionData = null;
    Map<String, Set<String>> borders = null;
    Map<String, Continent> continents = null;
    Map<String, Country> countries = null;
    String mapName = null;
    Scanner scanner = new Scanner(new File(fileName));
    ArrayList<String> continentsInOrder = new ArrayList<String>();
    ArrayList<String> countriesInOrder = new ArrayList<String>();
    Function<Scanner, ArrayList<String>> readSectionData = sc -> {
      ArrayList<String> sectionData = new ArrayList<>();
      while (sc.hasNext()) {
        String sectionLine = sc.nextLine();

        if (sectionLine.trim().isEmpty())
          break;
        sectionData.add(sectionLine);
      }
      return sectionData;
    };
    while (scanner.hasNext()) {
      String line = scanner.nextLine();
      if (line.startsWith("name")) {
        mapName = line;
        continue;
      }
      if (line.matches("\\[\\w*\\]")) {
        ArrayList<String> sectionData = readSectionData.apply(scanner);
        switch (line) {
          case "[files]":
            fileSectionData = sectionData;
            break;
          case "[continents]":
            continentsInOrder.addAll(sectionData.stream().map(ln -> ln.split(" ")[0])
                .collect(Collectors.toCollection(ArrayList::new)));
            continents = sectionData.stream().map(MapParser::mapContinent)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
            break;
          case "[countries]":
            countriesInOrder.addAll(sectionData.stream().map(ln -> ln.split(" ")[1])
                .collect(Collectors.toCollection(ArrayList::new)));
            countries = sectionData.stream().map(ln -> mapCountry(ln, continentsInOrder))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
            break;
          case "[borders]":
            borders = sectionData.stream().map(ln -> mapBorders(ln, countriesInOrder))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
            break;
        }
      }
    }
    scanner.close();
    if (countries == null || continents == null || borders == null || fileSectionData == null
        || mapName == null)
      throw new Exception("Map file is invalid");

    return new GameMap(fileSectionData, borders, continents, countries, mapName);
  }


  /**
   * Saves the map into a file.
   * 
   * @param gameMap The GameMap object to save.
   * @param fileName The name of the file.
   * @throws IOException TODO: will handle this.
   */
  public void saveMap(GameMap gameMap, String fileName) throws IOException {
    Files.write(Paths.get(fileName), serializeMap(gameMap).getBytes());
    System.out.println("Saved map to " + fileName + " successfully");
  }

  /**
   * Serializes a GameMap object into a string (file data).
   * 
   * @param gameMap The GameMap object to serialize.
   * @return The file as a string.
   */
  public String serializeMap(GameMap gameMap) {
    String files = gameMap.getFileSectionData().stream().collect(Collectors.joining("\n"));
    ArrayList<String> continentsOrder = gameMap.getContinents().keySet().stream().sorted()
        .collect(Collectors.toCollection(ArrayList::new));
    ArrayList<String> countriesOrder = gameMap.getCountries().keySet().stream().sorted()
        .collect(Collectors.toCollection(ArrayList::new));
    String continents = continentsOrder.stream().map(str -> gameMap.getContinents().get(str))
        .map(Continent::toString).collect(Collectors.joining("\n"));
    Function<Country, String> countryToString = country -> {
      int countryId = countriesOrder.indexOf(country.getName()) + 1;
      String countryName = country.getName();
      int continentId = continentsOrder.indexOf(country.getContinent()) + 1;
      return String.format("%d %s %d %d %d", countryId, countryName, continentId, country.getX(),
          country.getY());
    };
    String countries = countriesOrder.stream().map(str -> gameMap.getCountries().get(str))
        .map(countryToString).collect(Collectors.joining("\n"));

    String borders = countriesOrder.stream().map(countryName -> {
      int countryId = countriesOrder.indexOf(countryName) + 1;
      String neighbors =
          gameMap.getBorders().get(countryName).stream().map(c -> countriesOrder.indexOf(c) + 1)
              .map(String::valueOf).collect(Collectors.joining(" "));
      return String.format("%d %s", countryId, neighbors);
    }).collect(Collectors.joining("\n"));
    return String.format(
        "%s\n\n[files]\n%s\n\n[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n",
        gameMap.getFileName(), files, continents, countries, borders);
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

}
