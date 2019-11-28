package controllers;

import models.Country;
import models.GameMap;
import models.player.Player;
import org.junit.Before;
import org.junit.Test;
import utils.DominationMapParser;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * test class to check the functionalities of Runner.java {@link views.Runner}
 *
 * @author Sabari Venkadesh
 */
public class RunnerTest {

  /** list of players */
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
    MapParser mapParser = new DominationMapParser();
    gameMap = mapParser.loadMap(riskMap.getPath());
    reason = "";
    player1 = new Player("Player1", "human");
    player2 = new Player("Player2", "human");
    playersList.add(player1);
    playersList.add(player2);
    countries = new ArrayList<>(gameMap.getCountries().values());
  }

  /** Test to check whether the number of players are valid */
  @Test
  public void validatePlayerCount() {
    gameMap.setPlayersList(new ArrayList<>());
    gameMap.addGamePlayer("Player1", "human");
    boolean result = gameMap.validatePlayerCount();
    reason = "Player count should be greater than 2";
    assertFalse(reason, result);
    gameMap.addGamePlayer("Player2", "human");
    result = gameMap.validatePlayerCount();
    assertTrue(result);

    gameMap.addGamePlayer("Player3", "human");
    gameMap.addGamePlayer("Player4", "human");
    gameMap.addGamePlayer("Player5", "human");
    gameMap.addGamePlayer("Player6", "human");
    result = gameMap.validatePlayerCount();
    assertTrue(result);

    gameMap.addGamePlayer("player7", "human");
    result = gameMap.validatePlayerCount();
    reason = "Player count should be less than 6";
    assertFalse(reason, result);
  }
}
