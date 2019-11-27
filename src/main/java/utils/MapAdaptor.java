package utils;

import models.GameMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MapAdaptor {
  private ConquestMapParser conquestMapParser;
  private DominationMapParser dominationMapParser;

  public MapAdaptor() {
    this.conquestMapParser = new ConquestMapParser();
    this.dominationMapParser = new DominationMapParser();
  }

  public MapAdaptor(ConquestMapParser conquestMapParser) {
    this.conquestMapParser = conquestMapParser;
  }

  public MapAdaptor(DominationMapParser dominationMapParser) {
    this.dominationMapParser = dominationMapParser;
  }

  public GameMap autoLoadMap(String fileName) throws Exception {
    if (isMapTypeConquest(fileName)) {
      return conquestMapParser.loadMap(fileName);
    } else {
      return dominationMapParser.loadMap(fileName);
    }
  }

  public boolean autoSaveMap(GameMap gameMap, String fileName) throws Exception {
    if (gameMap.isMapTypeDomination()) {
      return dominationMapParser.saveMap(gameMap, fileName);
    } else {
      return conquestMapParser.saveMap(gameMap, fileName);
    }
  }

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
