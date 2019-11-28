package controllers;

import models.GameMap;
import models.player.Player;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import utils.DominationMapParser;
import utils.MapParser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
/**
 * test class to check the functionalities of TournamentController.java {@link TournamentController}
 *
 * @see controllers.TournamentController
 * @author Siddharth Singh
 */
public class TournamentControllerTest {
  /**
   * a property holding the max. number of turns for each game
   */
  public static int maxNumberOfTurnsProperty;

  /**
   * a map that holds the results table
   */
  private static Map<String, ArrayList<String>> resultTable = new HashMap<>();
  /** store game state */
  private GameMap gameMap;

  @Before
  public void setUp() throws Exception {
    File riskMap = new File("src/test/resources/risk.map");
    MapParser mapParser = new DominationMapParser();
    gameMap = mapParser.loadMap(riskMap.getPath());
  }

  @Test
  public void startTournament() {
    Path currentRelativePath = Paths.get("src/test/resources/risk.map");
    String mapPath = currentRelativePath.toAbsolutePath().toString();
    String command = String.format("tournament -M %s -P cheater benevolent -G 1 -D 50",mapPath);
    TournamentController.startTournament(gameMap, command);
    resultTable = TournamentController.resultTable;
    String winner = resultTable.values().stream().findFirst().get().get(0);
    assertThat(winner, containsString("CHEATER"));
  }
}