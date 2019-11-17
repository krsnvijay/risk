package utils;

import models.GameMap;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.either;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * test class to check the functionalities of MapParser.java {@link MapParser}
 *
 * @author SabariVenkadesh
 */
public class MapParserTest {

  /**
   * gameMap loads the invalid risk map from the resources folder
   */
  private GameMap gameMap;
  /** reason displays the reason for the failed condition */
  private String reason;

  /**
   * performs loading map from resources folder
   *
   * @throws Exception when map location is invalid
   */
  @Before
  public void setUp() throws Exception {
    // Load Risk map from resource folder
    File riskMap = new File("src/test/resources/risk.map");
    MapParser mapParser = new DominationMapParser();
    gameMap = mapParser.loadMap(riskMap.getPath());
    reason = "";
  }
  /**
   * checks the map has been loaded and parsed successfully
   *
   * @throws Exception when map location is invalid
   */
  @Test
  public void loadMap() throws Exception {
    GameMap gameMapInvalid = null;
    File testMap = new File("src/test/resources/mapparserinvalid.map");
    MapParser mapParser = new DominationMapParser();
    gameMapInvalid = mapParser.loadMap(testMap.getPath());
    Scanner scan = new Scanner(testMap);
    StringBuilder sb = new StringBuilder();
    String temp = "", str = "";
    while (scan.hasNext()) {
      sb.append(scan.nextLine());
    }

    assertThat(
        sb.toString(),
        either(containsString("name"))
            .or(containsString("[file]"))
            .or(containsString("[countries]"))
            .or(containsString("[continents]"))
            .or(containsString("[borders]")));
    scan.close();
  }

  /**
   * checks the map has been saved in a file successfully
   *
   * @throws IOException when file location is invalid
   */
  @Test
  public void saveMap() throws IOException {
    DominationMapParser mapParser = new DominationMapParser();
    Files.write(
        Paths.get("src/test/resources/mapparsersavetest.map"),
        mapParser.serializeMap(gameMap, "mapparsersavetest").getBytes());
    File saveTestFile = new File("src/test/resources/mapparsersavetest.map");
    assertTrue(saveTestFile.exists());
  }
}
