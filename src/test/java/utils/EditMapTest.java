package utils;

import models.GameMap;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;

public class EditMapTest {
  private GameMap gameMap;
  private String reason;

  @Before
  public void setUp() throws Exception {
    // Load Risk map from resource folder
    File riskMap = new File("src/test/resources/riskContinentnotasubgraph.map");
    gameMap = MapParser.loadMap(riskMap.getPath());
    reason = "";
  }

  @Test
  public void validateSubgraphConnectivityMap() {

    EditMap editMap = new EditMap();
    boolean isMapValid = editMap.validateMap(gameMap);
    boolean iSContinentASubGraph = editMap.DFSCheckOnContinent(gameMap);
    reason = "The map is invalid";
    assertFalse(reason, isMapValid);
    reason = "The continent is not a sub graph";
    assertFalse(reason, iSContinentASubGraph);

  }
}