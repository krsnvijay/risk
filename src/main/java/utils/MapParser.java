package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
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
  public GameMap loadMap(String fileName) throws IOException, Exception {
    HashMap<String, ArrayList<String>> mapData = new HashMap<>();
    String mapName = null;
    Scanner scanner = new Scanner(new File(fileName));
    while (scanner.hasNext()) {
      String line = scanner.nextLine();
      if (line.startsWith("name")) {
        mapName = line;
        continue;
      }
      if (line.matches("\\[\\w*\\]")) {
        ArrayList<String> sectionData = new ArrayList<>();
        while (scanner.hasNext()) {
          String sectionLine = scanner.nextLine();

          if (sectionLine.trim().isEmpty())
            break;
          sectionData.add(sectionLine);
        }
        mapData.put(line, sectionData);
      }
    }
    scanner.close();
    if (!mapData.containsKey("[countries]") || !mapData.containsKey("[continents]")
        || !mapData.containsKey("[borders]") || mapName == null)
      throw new Exception("Map file is invalid");
    return new GameMap(mapName, mapData);
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
}
