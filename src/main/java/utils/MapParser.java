package utils;

import models.GameMap;

/**
 * The Map Parser utility parses the whole map file from disk.
 *
 * @author Vijay
 * @version 1.0
 */
public interface MapParser {
  /**
   * The loadMap reads a file and parses it into a gameMap object
   *
   * @param fileLocation path of the map file
   * @return GameMap object that is parsed
   * @throws Exception if the map file is invalid or if it does not exist
   */
  GameMap loadMap(String fileLocation) throws Exception;

  /**
   * The saveMap serializes a gameMap and saves it to a file
   *
   * @param gameMap contains state of the game
   * @param fileLocation path to save the file at
   * @return boolean to indicate status
   */
  boolean saveMap(GameMap gameMap, String fileLocation);
}
