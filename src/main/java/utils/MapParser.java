package utils;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import models.Continent;
import models.Country;
import models.GameMap;

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
    ArrayList<String> continentsInOrder = new ArrayList<>();
    ArrayList<String> countriesInOrder = new ArrayList<>();
    Function<Scanner, ArrayList<String>> readSectionData =
        sc -> {
          ArrayList<String> sectionData = new ArrayList<>();
          while (sc.hasNext()) {
            String sectionLine = sc.nextLine();

            if (sectionLine.trim().isEmpty()) break;
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
      if (line.matches("\\[\\w*]")) {
        ArrayList<String> sectionData = readSectionData.apply(scanner);
        switch (line) {
          case "[files]":
            fileSectionData = sectionData;
            break;
          case "[continents]":
            continentsInOrder.addAll(
                sectionData.stream()
                    .map(ln -> ln.split(" ")[0])
                    .collect(toCollection(ArrayList::new)));
            continents =
                sectionData.stream()
                    .map(MapParser::deserializeContinent)
                    .collect(toMap(Continent::getName, Function.identity()));
            break;
          case "[countries]":
            countriesInOrder.addAll(
                sectionData.stream()
                    .map(ln -> ln.split(" ")[1])
                    .collect(toCollection(ArrayList::new)));
            countries =
                sectionData.stream()
                    .map(ln -> deserializeCountry(ln, continentsInOrder))
                    .collect(toMap(Country::getName, Function.identity()));
            break;
          case "[borders]":
            borders =
                sectionData.stream()
                    .map(ln -> deserializeBorder(ln, countriesInOrder))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
            break;
        }
      }
    }
    scanner.close();
    if (countries == null
        || continents == null
        || borders == null
        || fileSectionData == null
        || mapName == null) throw new Exception("Map file is invalid");

    return new GameMap(fileSectionData, borders, continents, countries, mapName);
  }

  /**
   * This deserializes the country line from the file into a Country object.
   *
   * @param countryLine the raw line from the file.
   * @param continentNames the names of all continents
   * @return A continent object
   */
  private static Country deserializeCountry(String countryLine, ArrayList<String> continentNames) {
    String[] splitCountryLine = countryLine.split(" ");
    String name = splitCountryLine[1];
    String continent = continentNames.get(Integer.parseInt(splitCountryLine[2]) - 1);
    int x = Integer.parseInt(splitCountryLine[3]);
    int y = Integer.parseInt(splitCountryLine[4]);
    return new Country(name, continent, x, y);
  }

  /**
   * This deserializes the continent line from the file into a Continent object.
   *
   * @param continentLine the raw line from the file.
   * @return A continent object
   */
  private static Continent deserializeContinent(String continentLine) {
    String[] splitContinentLine = continentLine.split(" ");
    String name = splitContinentLine[0];
    int value = Integer.parseInt(splitContinentLine[1]);
    String color = splitContinentLine[2];
    return new Continent(color, name, value);
  }

  /**
   * This deserializes the border line from the file into a Border object.
   *
   * @param borderLine the raw line from the file.
   * @param countryNames A list of country names.
   * @return An entry object, key is the name and the value is a Set of borders.
   */
  private static Map.Entry<String, Set<String>> deserializeBorder(
      String borderLine, ArrayList<String> countryNames) {
    String[] splitBorderLine = borderLine.split(" ");
    String key = countryNames.get(Integer.parseInt(splitBorderLine[0]) - 1);
    Set<String> value =
        Arrays.stream(splitBorderLine, 1, splitBorderLine.length)
            .map(str -> countryNames.get(Integer.parseInt(str) - 1))
            .collect(toSet());
    return new AbstractMap.SimpleEntry<>(key, value);
  }

  /**
   * Serialize a Continent
   *
   * @param continent continent object to serialize
   * @return formatted continent line
   */
  private static String serializeContinent(Continent continent) {
    return String.format(
        "%s %d %s", continent.getName(), continent.getValue(), continent.getColor());
  }

  /**
   * Serialize a Country
   *
   * @param country country object to serialize
   * @param countriesOrder ordered countries list to build indexes
   * @param continentsOrder ordered continent list to build indexes
   * @return formatted country line
   */
  private static String serializeCountry(
      Country country, ArrayList<String> countriesOrder, ArrayList<String> continentsOrder) {
    int countryId = countriesOrder.indexOf(country.getName()) + 1;
    String countryName = country.getName();
    int continentId = continentsOrder.indexOf(country.getContinent()) + 1;
    return String.format(
        "%d %s %d %d %d", countryId, countryName, continentId, country.getX(), country.getY());
  }

  /**
   * Serialize a Border
   *
   * @param countryName name of country
   * @param countriesOrder ordered countries list to build indexes
   * @param gameBorders map of borders
   * @return formatted border line
   */
  private static String serializeBorder(
      String countryName, ArrayList<String> countriesOrder, Map<String, Set<String>> gameBorders) {
    int countryId = countriesOrder.indexOf(countryName) + 1;
    String neighbors =
        gameBorders.get(countryName).stream()
            .map(countriesOrder::indexOf)
            .map(i -> i + 1)
            .sorted()
            .map(String::valueOf)
            .collect(joining(" "));
    return String.format("%d %s", countryId, neighbors);
  }

  /**
   * Saves the map into a file.
   *
   * @param gameMap The GameMap object to save.
   * @param fileName The name of the file.
   * @throws IOException TODO: will handle this.
   */
  public static void saveMap(GameMap gameMap, String fileName) throws IOException {
    Files.write(Paths.get(fileName), serializeMap(gameMap).getBytes());
    System.out.println("Saved map to " + fileName + " successfully");
  }

  /**
   * Serializes a GameMap object into a string (file data).
   *
   * @param gameMap The GameMap object to serialize.
   * @return The file as a string.
   */
  public static String serializeMap(GameMap gameMap) {
    // Get required data to serialize
    String files = String.join("\n", gameMap.getFileSectionData());
    Map<String, Country> gameCountries = gameMap.getCountries();
    Map<String, Continent> gameContinents = gameMap.getContinents();
    Map<String, Set<String>> gameBorders = gameMap.getBorders();

    // Get sorted string keys to build integer indexes
    ArrayList<String> continentsOrder =
        gameContinents.keySet().stream().sorted().collect(toCollection(ArrayList::new));
    ArrayList<String> countriesOrder =
        gameCountries.keySet().stream().sorted().collect(toCollection(ArrayList::new));

    // collect serialized continents
    String continents =
        continentsOrder.stream()
            .map(gameContinents::get)
            .map(MapParser::serializeContinent)
            .collect(joining("\n"));

    // collect serialized countries
    String countries =
        countriesOrder.stream()
            .map(gameCountries::get)
            .map(c -> serializeCountry(c, countriesOrder, continentsOrder))
            .collect(joining("\n"));

    // collect serialized borders
    String borders =
        countriesOrder.stream()
            .map(c -> serializeBorder(c, countriesOrder, gameBorders))
            .collect(joining("\n"));

    // serialize gameMap to Risk Map format
    return String.format(
        "%s\n\n[files]\n%s\n\n[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n",
        gameMap.getFileName(), files, continents, countries, borders);
  }
}
