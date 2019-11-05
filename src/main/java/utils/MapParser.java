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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import models.Card;
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
   * @throws Exception when map is invalid
   */
  public static GameMap loadMap(String fileName) throws Exception {
    ArrayList<String> fileSectionData = new ArrayList<>();
    Map<String, Set<String>> borders = new HashMap<>();
    Map<String, Continent> continents = new HashMap<>();
    Map<String, Country> countries = new HashMap<>();
    String mapName = "";
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
    Scanner scanner = new Scanner(new File(fileName));
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
    if (countries.size() == 0
        || continents.size() == 0
        || borders.size() == 0
        || fileSectionData.size() == 0
        || mapName.isEmpty()) {
      throw new Exception("Invalid map");
    }

    GameMap gameMap = GameMap.getGameMap();
    fileName = String.format("name %s map", fileName);
    gameMap.populateGameMap(fileSectionData, borders, continents, countries, fileName);
    return gameMap;
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
   * @return boolean to indicate status
   */
  public static boolean saveMap(GameMap gameMap, String fileName) {
    boolean result = false;
    try {
      Files.write(Paths.get(fileName), serializeMap(gameMap, fileName).getBytes());
      result = true;
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    return result;
  }

  /**
   * Serializes a GameMap object into a string (file data).
   *
   * @param gameMap The GameMap object to serialize.
   * @param filename the filename of file to which the map is to be saved
   * @return The file as a string.
   */
  public static String serializeMap(GameMap gameMap, String filename) {
    // Get required data to serialize
    String files =
        gameMap.getFileName().isEmpty()
            ? String.join(
            "\n",
            "pic placeholder_pic.png",
            "map placeholder_map.gif",
            "crd placeholder.cards",
            "prv placeholder.jpg")
            : String.join("\n", gameMap.getFileSectionData());

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

    if (gameMap.getFileName().isEmpty()) {
      gameMap.setFileName(String.format("name %s map", filename));
    }

    // serialize gameMap to Risk Map format
    return String.format(
        "%s\n\n[files]\n%s\n\n[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n",
        gameMap.getFileName(), files, continents, countries, borders);
  }

  /*
    public static void cardsToCountries(GameMap gameMap){
      Map<String, Country> countriesWithCards = gameMap.getCountries().values().stream().map((country) -> {
        ArrayList<Player> filteredPlayer = gameMap.getPlayersList().stream().filter((player) -> player.getPlayerName()
                .equals(country.getOwnerName())).collect(toCollection(ArrayList::new));
        country.setCard(new Card(filteredPlayer.get(0),country));
        return country;
      }).collect(toMap(Country::getName, country -> country));
      gameMap.setCountries(countriesWithCards);
    }
  */

  /**
   * Builds the deck of RISK cards
   *
   * @param gameMap The GameMap object to save.
   */
  public static void buildDeck(GameMap gameMap) {
    ArrayList<Country> countriesInMap = new ArrayList<>(gameMap.getCountries().values());
    ArrayList<Card> cardsInDeck = new ArrayList<>();
    for (Country country : countriesInMap) {
      cardsInDeck.add(new Card(country.getName()));
    }
    Collections.shuffle(cardsInDeck);
    gameMap.setDeck(cardsInDeck);
  }
}
