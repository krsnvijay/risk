package utils;

import models.GameMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/** Adaptor class for conquest, domination maps */
public class MapAdaptor {
  /** Parser for the conquest map */
  private ConquestMapParser conquestMapParser;
  /** Parser for the domination map */
  private DominationMapParser dominationMapParser;

  /** Constructor for map adaptor */
  public MapAdaptor() {
    this.conquestMapParser = new ConquestMapParser();
    this.dominationMapParser = new DominationMapParser();
  }

  /**
   * Overloaded Constructor for mapAdaptor that stores conquestMapParser
   *
   * @param conquestMapParser MapParser object for conquest
   */
  public MapAdaptor(ConquestMapParser conquestMapParser) {
    this.conquestMapParser = conquestMapParser;
  }

  /**
   * Overloaded constructor for mapAdaptor that stores domination parser
   *
   * @param dominationMapParser MapParser object for domination
   */
  public MapAdaptor(DominationMapParser dominationMapParser) {
    this.dominationMapParser = dominationMapParser;
  }

  /**
   * Automatically loads map file based on the content inside of them
   *
   * @param fileName path of the map file
   * @return parsed GameMap object
   * @throws Exception file read exception
   */
  public GameMap autoLoadMap(String fileName) throws Exception {
    GameMap gameMap;
    if (isMapTypeConquest(fileName)) {
      gameMap = conquestMapParser.loadMap(fileName);
    } else {
      gameMap = dominationMapParser.loadMap(fileName);
    }
      return gameMap;
  }

  /**
   * Automatically save map based on the original loaded map type
   *
   * @param gameMap contains game state
   * @param fileName path to file
   * @return boolean to indicate success for the save operation
   * @throws Exception if save failed because of file access
   */
  public boolean autoSaveMap(GameMap gameMap, String fileName) throws Exception {
    if (gameMap.isMapTypeDomination()) {
      return dominationMapParser.saveMap(gameMap, fileName);
    } else {
      return conquestMapParser.saveMap(gameMap, fileName);
    }
  }

  /**
   * Gets the type of the map file by reading its contents
   *
   * @param fileName path to map file
   * @return true if map type is conquest false if map type is domination
   * @throws FileNotFoundException if path location is invalid
   */
  private boolean isMapTypeConquest(String fileName) throws FileNotFoundException {
    Scanner sc = new Scanner(new File(fileName));
    while (sc.hasNext()) {
      String line = sc.nextLine();
      if (line.matches("\\[\\w*]")) {
        return line.equals("[Map]");
      }
    }
    return false;
  }
}
