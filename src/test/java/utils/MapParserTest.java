package utils;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.either;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import models.GameMap;
import org.junit.Before;
import org.junit.Test;

public class MapParserTest {

  private GameMap gameMap;
  private String reason;

  @Before
  public void setUp() throws Exception {
    // Load Risk map from resource folder
    File riskMap = new File("src/test/resources/risk.map");
    gameMap = MapParser.loadMap(riskMap.getPath());
    reason = "";
  }

  @Test
  public void loadMap() throws Exception {
    GameMap gameMapInvalid = new GameMap();
    File testMap = new File("src/test/resources/mapparserinvalid.map");
    gameMapInvalid = MapParser.loadMap(testMap.getPath());
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

  @Test
  public void saveMap() throws IOException {
    Files.write(
        Paths.get("src/test/resources/mapparsersavetest.map"),
        MapParser.serializeMap(gameMap).getBytes());
    File saveTestFile = new File("src/test/resources/mapparsersavetest.map");
    assertTrue(saveTestFile.exists());
  }
}

