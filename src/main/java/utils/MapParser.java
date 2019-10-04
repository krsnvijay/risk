package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
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
    Files.write(Paths.get(fileName), gameMap.serializeMap().getBytes());
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
      System.out.println(testMap.serializeMap());
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
