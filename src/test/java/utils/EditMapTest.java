package utils;

import models.GameMap;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;

/**
 * test class to check the functionalities of EditMap.java
 *
 * @author SabariVenkadesh
 */
public class EditMapTest {

  /**
   * gameMap loads the invalid risk map from the resources folder reason displays the reason for the
   * failed condition
   */
  private GameMap gameMap;
  /** displays the reason for the failed condition */
  private String reason;

  /**
   * performs loading invalid map and storing it in riskMap
   *
   * @throws Exception if map is invalid
   */
  @Before
  public void setUp() throws Exception {
    // Load Risk map from resource folder
    File riskMap = new File("src/test/resources/riskContinentnotasubgraph.map");
    gameMap = MapParser.loadMap(riskMap.getPath());
    reason = "";
  }

  /** performs check on the map validity and DFS check on the continent */
  @Test
  public void validateSubgraphConnectivityMap() {

    boolean isMapValid = EditMap.validateMap(gameMap);
    boolean iSContinentASubGraph = EditMap.DFSCheckOnContinent(gameMap);
    reason = "The map is invalid";
    assertFalse(reason, isMapValid);
    reason = "The continent is not a sub graph";
    assertFalse(reason, iSContinentASubGraph);
  }
}
