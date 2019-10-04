package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;
import models.Continent;
import models.GameMap;

/** @author v_valla */
public class MapParser {
  /**
   * @param fileName the location of the map file to be parsed
   * @return GameMap the parsed GameMap Object
   * @throws IOException when file location/contents are invalid
   * @throws Exception when map file does'nt contain all the required sections
   */
  public static GameMap loadMap(String fileName) throws IOException, Exception {
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

  public static void saveMap(GameMap gameMap, String fileName) throws IOException {
    Files.write(Paths.get(fileName), serializeMap(gameMap).getBytes());
  }

  public static String serializeMap(GameMap gameMap) {
    String files = gameMap.getFileSectionData().stream().collect(Collectors.joining("\n"));
    ArrayList<String> continentsOrder = gameMap.getContinents().keySet().stream().sorted()
        .collect(Collectors.toCollection(ArrayList::new));
    ArrayList<String> countriesOrder = gameMap.getCountries().keySet().stream().sorted()
        .collect(Collectors.toCollection(ArrayList::new));
    String continents = continentsOrder.stream().map(str -> gameMap.getContinents().get(str))
        .map(Continent::toString).collect(Collectors.joining("\n"));
    String countries =
        countriesOrder.stream().map(str -> gameMap.getCountries().get(str)).map(country -> {
          int countryId = countriesOrder.indexOf(country.getName()) + 1;
          String countryName = country.getName();
          int continentId = continentsOrder.indexOf(country.getContinent()) + 1;
          return String.format("%d %s %d %d %d", countryId, countryName, continentId,
              country.getX(), country.getY());
        }).collect(Collectors.joining("\n"));

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

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Invalid argument");
      System.out.println("Usage : MapParser <map_location> <map_location_to_save(optional)>");
      System.exit(-1);
    }
    try {
      GameMap testMap = loadMap(args[0]);
      testMap.showMapByContinents();
      System.out.println();
      System.out.println(serializeMap(testMap));
      if (args.length == 2)
        saveMap(testMap, args[1]);
      // Scanner keyboard = new Scanner(System.in);
      // while (true) {
      // String command = keyboard.nextLine();

      // String[] commandSplit_original = command.split(" -");
      // String[] commandSplit =
      // Arrays.copyOfRange(commandSplit_original, 1, commandSplit_original.length);

      // if (command.startsWith("editcontinent")) {
      // if (!EditMap.editContinent(commandSplit, testMap)) {
      // System.out.println("Failed to perform certain continent operations");
      // }

      // } else if (command.startsWith("editcountry")) {
      // if (!EditMap.editCountry(commandSplit, testMap)) {
      // System.out.println("Failed to perform certain country operations");
      // }

      // } else if (command.startsWith("editneighbor")) {
      // if (!EditMap.editNeighbor(commandSplit, testMap)) {
      // System.out.println("Failed to perform certain neighbor country operations");
      // }

      // } else {
      // if (command.startsWith("showmap")) {
      // System.out.println(testMap);
      // } else {
      // System.out.println("INVALID COMMAND! TRY AGAIN!");
      // continue;
      // }
      // }
      // }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
