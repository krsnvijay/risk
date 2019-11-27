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
 * test class to check the functionalities of ConquestMapParser.java {@link ConquestMapParser}
 *
 * @author Vijay Krishna
 */
public class ConquestMapParserTest {
  /**
   * gameMap loads the invalid risk map from the resources folder
   */
  private GameMap gameMap;
  /**
   * reason displays the reason for the failed condition
   */
  private String reason;

  /**
   * performs loading map from resources folder
   *
   * @throws Exception when map location is invalid
   */
  @Before
  public void setUp() throws Exception {
    // Load Risk map from resource folder
    File riskMap = new File("src/test/resources/conquest-world.map");
    MapParser mapParser = new ConquestMapParser();
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
    GameMap gameMap = null;
    File testMap = new File("src/test/resources/conquest-world.map");
    MapParser mapParser = new ConquestMapParser();
    gameMap = mapParser.loadMap(testMap.getPath());
    Scanner scan = new Scanner(testMap);
    StringBuilder sb = new StringBuilder();
    String temp = "", str = "";
    while (scan.hasNext()) {
      sb.append(scan.nextLine());
    }

    assertThat(
        sb.toString(),
        either(containsString("[Map]"))
            .or(containsString("[Continents]"))
            .or(containsString("[Territories]")));
    scan.close();
  }

  @Test
  public void saveMap() throws IOException {
    ConquestMapParser mapParser = new ConquestMapParser();
    Files.write(
        Paths.get("src/test/resources/conquestmapparsersavetest.map"),
        mapParser.serializeMap(gameMap, "conquestmapparsersavetest").getBytes());
    File saveTestFile = new File("src/test/resources/conquestmapparsersavetest.map");
    assertTrue(saveTestFile.exists());
  }
}