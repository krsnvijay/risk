package utils;

import models.GameMap;

/**
 * The Map Parser utility parses the whole map file from disk.
 *
 * @author Vijay
 * @version 1.0
 */
public interface MapParser {
  GameMap loadMap(String fileLocation) throws Exception;

  boolean saveMap(GameMap gameMap, String fileLocation);
}
