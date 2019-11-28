package utils;

import models.Context;
import models.Country;
import models.GameMap;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class to check the functionalities of game save/load  {@link GamePersistenceHandler}
 *
 * @author Vijay Krishna
 */
public class GamePersistenceHandlerTest {
  public static final String PLAYER_1 = "Player1";
  public static final String PLAYER_2 = "Player2";

  /**
   * player name
   */
  String playerName;
  /**
   * reason for failure
   */
  String reason;

  /**
   * list of countries
   */
  Map<String, Country> countries;

  /**
   * store game state
   */
  private GameMap gameMap;

  /**
   * Sets up context for the test
   *
   * @throws Exception when map file is invalid
   */
  @Before
  public void setUp() throws Exception {
    File riskMap = new File("src/test/resources/risk.map");
    MapParser mapParser = new DominationMapParser();
    gameMap = mapParser.loadMap(riskMap.getPath());
    reason = "";
    gameMap.setPlayersList(new ArrayList<>());
    gameMap.addGamePlayer(PLAYER_1, "human");
    gameMap.addGamePlayer(PLAYER_2, "human");
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    gameMap.placeAll();
    GameMap.setCurrentPlayerIndex(0);
  }

  /**
   * Tests savegame functionality
   * Test passes if the gameState is saved to a file as json
   *
   * @throws IOException when file not found
   */
  @Test
  public void saveState() throws IOException {
    File file = File.createTempFile("savegame-test", ".json", new File("src/test/resources/"));
    file.deleteOnExit();
    GamePersistenceHandler.saveState(file.getPath());
    File saveTestFile = new File(file.getPath());
    reason = "Temp savegame file should exist after save";
    assertTrue(reason, saveTestFile.exists());

  }

  /**
   * Test loadgame functionality
   * Test passes when the loaded game state is same as the original
   *
   * @throws IOException when file not found
   */
  @Test
  public void loadState() throws IOException {
    File file = File.createTempFile("savegame-test", ".json", new File("src/test/resources/"));
    file.deleteOnExit();
    int noOfCountries = gameMap.getCountries().size();
    String currentPlayerName = gameMap.getCurrentPlayer().getStrategy().getPlayerName();
    Context context = gameMap.getCurrentContext();
    GamePersistenceHandler.saveState(file.getPath());
    GameMap.modifyInstance(null);
    GamePersistenceHandler.loadState(file.getPath());
    reason = "Number of countries should be same as original";
    assertEquals(reason, noOfCountries, gameMap.getCountries().size());
    reason = "Current Player should be same";
    assertEquals(reason, currentPlayerName, gameMap.getCurrentPlayer().getStrategy().getPlayerName());
    reason = "Current Context should be the same";
    assertEquals(reason, context, gameMap.getCurrentContext());
  }
}