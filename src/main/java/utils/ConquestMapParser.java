package utils;

import models.Continent;
import models.Country;
import models.GameMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

/** Parses - (Serialize,Deserializes) risk map of type Conquest */
public class ConquestMapParser implements MapParser {
  /**
   * Loads the map file and parses it.
   *
   * @param fileName the location of the map file to be parsed
   * @return GameMap the parsed GameMap Object
   * @throws Exception when map is invalid
   */
  @Override
  public GameMap loadMap(String fileName) throws Exception {
    ArrayList<String> fileSectionData = new ArrayList<>();
    Map<String, Set<String>> borders = new HashMap<>();
    Map<String, Continent> continents = new HashMap<>();
    Map<String, Country> countries = new HashMap<>();
    Function<Scanner, ArrayList<String>> readSectionData =
        sc -> {
          ArrayList<String> sectionData = new ArrayList<>();
          while (sc.hasNext()) {
            String sectionLine = sc.nextLine();

            if (sectionLine.trim().isEmpty()) {
              break;
            }
            sectionData.add(sectionLine);
          }
          return sectionData;
        };
    Scanner scanner = new Scanner(new File(fileName));
    while (scanner.hasNext()) {
      String line = scanner.nextLine();
      if (line.matches("\\[\\w*]")) {
        ArrayList<String> sectionData = readSectionData.apply(scanner);
        switch (line) {
          case "[Map]":
            fileSectionData = sectionData;
            break;
          case "[Continents]":
            continents =
                sectionData.stream()
                    .map(this::deserializeContinent)
                    .collect(toMap(Continent::getName, Function.identity()));
            break;
          case "[Territories]":
            do {
              countries.putAll(
                  sectionData.stream()
                      .map(this::deserializeCountry)
                      .collect(toMap(Country::getName, Function.identity())));
              borders.putAll(
                  sectionData.stream()
                      .map(this::deserializeBorder)
                      .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
              sectionData = readSectionData.apply(scanner);
            } while (sectionData.size() != 0);
            break;
        }
      }
    }
    scanner.close();
    if (countries.size() == 0
        || continents.size() == 0
        || borders.size() == 0
        || fileSectionData.size() == 0) {
      throw new Exception("Invalid map");
    }

    GameMap gameMap = GameMap.getGameMap();
    fileName = String.format("name %s map", fileName);
    gameMap.populateGameMap(fileSectionData, borders, continents, countries, fileName);
    gameMap.setMapTypeDomination(false);
    return gameMap;
  }

  /**
   * This de-serializes the country line from the file into a Country object.
   *
   * @param countryLine the raw line from the file.
   * @return A continent object
   */
  private Country deserializeCountry(String countryLine) {
    String[] splitCountryLine = countryLine.split(",");
    String name = splitCountryLine[0].replace(' ', '-');
    String continent = splitCountryLine[3].replace(' ', '-');
    int x = Integer.parseInt(splitCountryLine[1]);
    int y = Integer.parseInt(splitCountryLine[2]);
    return new Country(name, continent, x, y);
  }

  /**
   * This de-serializes the continent line from the file into a Continent object.
   *
   * @param continentLine the raw line from the file.
   * @return A continent object
   */
  private Continent deserializeContinent(String continentLine) {
    String[] splitContinentLine = continentLine.split("=");
    String name = splitContinentLine[0].replace(' ', '-');
    int value = Integer.parseInt(splitContinentLine[1]);
    return new Continent(name, value);
  }

  /**
   * This de-serializes the border line from the file into a Border object.
   *
   * @param borderLine the raw line from the file.
   * @return An entry object, key is the name and the value is a Set of borders.
   */
  private Map.Entry<String, Set<String>> deserializeBorder(String borderLine) {
    String[] splitBorderLine = borderLine.split(",");
    String key = splitBorderLine[0].replace(' ', '-');
    Set<String> value =
        Arrays.stream(splitBorderLine, 4, splitBorderLine.length)
            .map(str -> str.replace(' ', '-'))
            .collect(toSet());
    return new AbstractMap.SimpleEntry<>(key, value);
  }

  /**
   * Serialize a Continent
   *
   * @param continent continent object to serialize
   * @return formatted continent line
   */
  private String serializeContinent(Continent continent) {
    return String.format("%s=%d", continent.getName().replace('-', ' '), continent.getValue());
  }

  /**
   * Serialize a Country
   *
   * @param country country object to serialize
   * @param neighbors neighbors of the country
   * @return formatted country line
   */
  private String serializeCountry(Country country, Set<Country> neighbors) {
    String countryName = country.getName();
    String continentName = country.getContinent();
    return String.format(
        "%s,%d,%d,%s,%s",
        countryName.replace('-', ' '),
        country.getX(),
        country.getY(),
        continentName.replace('-', ' '),
        serializeBorder(neighbors));
  }

  /**
   * Serialize a Border
   *
   * @param neighbors neighbors to serialize
   * @return formatted border line
   */
  private String serializeBorder(Set<Country> neighbors) {
    return neighbors.stream()
        .map(Country::getName)
        .map(str -> str.replace('-', ' '))
        .collect(joining(","));
  }

  /**
   * Saves the map into a file.
   *
   * @param gameMap The GameMap object to save.
   * @param fileName The name of the file.
   * @return boolean to indicate status
   */
  @Override
  public boolean saveMap(GameMap gameMap, String fileName) {
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
  public String serializeMap(GameMap gameMap, String filename) {
    // Get required data to serialize
    String files =
        gameMap.getFileName().isEmpty()
            ? String.join(
                "\n",
                "author=NoOne",
                "image=noImage.bmp",
                "wrap=no",
                "scroll=horizontal",
                "warn=yes")
            : String.join("\n", gameMap.getFileSectionData());

    Map<String, Country> gameCountries = gameMap.getCountries();
    Map<String, Continent> gameContinents = gameMap.getContinents();
    Map<String, Set<Country>> gameBorders =
        gameMap.getBorders().entrySet().stream()
            .collect(
                toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().stream().sorted().map(gameCountries::get).collect(toSet())));

    // Get sorted string keys for serializing the map
    ArrayList<String> continentsOrder =
        gameContinents.keySet().stream().sorted().collect(toCollection(ArrayList::new));
    ArrayList<String> countriesOrder =
        gameCountries.keySet().stream().sorted().collect(toCollection(ArrayList::new));

    // collect serialized continents
    String continents =
        continentsOrder.stream()
            .map(gameContinents::get)
            .map(this::serializeContinent)
            .collect(joining("\n"));

    // collect serialized territories
    String territories =
        continentsOrder.stream()
            .map(
                continent ->
                    gameCountries.values().stream()
                        .filter(country -> country.getContinent().equals(continent))
                        .map(
                            country ->
                                serializeCountry(country, gameBorders.get(country.getName())))
                        .collect(joining("\n")))
            .collect(joining("\n\n"));

    // serialize gameMap to Risk Map format
    return String.format(
        "[Map]\n%s\n\n[Continents]\n%s\n\n[Territories]\n%s", files, continents, territories);
  }
}
