package controllers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import models.Country;
import models.GameMap;
import models.Player;
import org.junit.Before;
import org.junit.Test;
import utils.MapParser;

/**
 * test class to check the functionalities of GameRunner.java
 *
 * @author Sabari Venkadesh
 */
public class GameRunnerTest {

  /** players in the game should either be more than 2 and less than 6 */
  /** list of playes */
  private static ArrayList<Player> playersList = new ArrayList<>();
  /** player name */
  String playerName;
  /** reason for failure */
  String reason;
  /** list of countries */
  ArrayList<Country> countries;

  /** store game state */
  private GameMap gameMap;
  /** store player 1 */
  private Player player1;
  /** store player 2 */
  private Player player2;

  /**
   * Sets up context for the test
   *
   * @throws Exception when map file is invalid
   */
  @Before
  public void setUp() throws Exception {
    File riskMap = new File("src/test/resources/risk.map");
    gameMap = MapParser.loadMap(riskMap.getPath());
    reason = "";
    player1 = new Player("Player1");
    player2 = new Player("Player2");
    playersList.add(player1);
    playersList.add(player2);
    countries = new ArrayList<>(gameMap.getCountries().values());
  }

  /** Test to check whether the number of players are valid */
  @Test
  public void validatePlayerCount() {
    gameMap.setPlayersList(new ArrayList<>());
    gameMap.addGamePlayer("Player1");
    boolean result = gameMap.validatePlayerCount();
    reason = "Player count should be greater than 2";
    assertFalse(reason, result);
    gameMap.addGamePlayer("Player2");
    result = gameMap.validatePlayerCount();
    assertTrue(result);

    gameMap.addGamePlayer("Player3");
    gameMap.addGamePlayer("Player4");
    gameMap.addGamePlayer("Player5");
    gameMap.addGamePlayer("Player6");
    result = gameMap.validatePlayerCount();
    assertTrue(result);

    gameMap.addGamePlayer("player7");
    result = gameMap.validatePlayerCount();
    reason = "Player count should be less than 6";
    assertFalse(reason, result);
  }
}
