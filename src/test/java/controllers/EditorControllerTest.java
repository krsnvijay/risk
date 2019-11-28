package controllers;

import models.GameMap;
import org.junit.Before;
import org.junit.Test;
import utils.DominationMapParser;
import utils.MapParser;
import utils.MapValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * test class to check the functionalities of EditorController.java {@link EditorController}
 *
 * @see controllers.EditorController
 * @author Sabari Venkadesh
 */
public class EditorControllerTest {
  private GameMap gameMap;
  private String reason;

  /**
   * Sets up context for the test
   *
   * @throws Exception when map file is invalid
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
 * test to validate the map
 */
  @Test
  public void validateMap() {
    //  Arrange
    File riskMap = new File("src/test/resources/riskinvalid.map");
    //  Act
    try {
      MapParser mapParser = new DominationMapParser();
      gameMap = mapParser.loadMap(riskMap.getPath());
      MapValidator mapValidator = new MapValidator();
      reason="Invalid map being loaded";
      // Assert
      assertFalse(reason, MapValidator.validateMap(gameMap));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  /**
   * test to save the map
   * @throws IOException if map file is invalid
   */
  @Test
  public void saveMap() throws IOException {
    DominationMapParser mapParser = new DominationMapParser();
    Files.write(
        Paths.get("src/test/resources/mapparsersavetest.map"),
        mapParser.serializeMap(gameMap, "mapparsersavetest").getBytes());
    File saveTestFile = new File("src/test/resources/mapparsersavetest.map");
    reason="The map has been saved successfully";
    assertTrue(reason,saveTestFile.exists());
  }

}
