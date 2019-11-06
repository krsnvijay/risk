package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import models.Context;
import models.Country;
import models.GameMap;
import models.Player;
import org.junit.Before;
import org.junit.Test;
import utils.MapParser;

/**
 * test class to check the functionalities of StartUpController.java
 *
 * @see controllers.StartUpController
 * @author Siddharth Singh
 */
class StartUpControllerTest {

  public static final String PLAYER_1 = "Player1";
  public static final String PLAYER_2 = "Player2";
  public static final String INDIA = "India";
  public static final String CHINA = "China";
  /** list of players */
  private static ArrayList<Player> playersList = new ArrayList<>();
  /** player name */
  String playerName;
  /** reason for failure */
  String reason;
  /** commands by the player */
  String command;

  /** list of countries */
  Map<String, Country> countries;

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
  void setUp() throws Exception {
    File riskMap = new File("src/test/resources/risk.map");
    gameMap = MapParser.loadMap(riskMap.getPath());
    reason = "";
    command = "";
    gameMap.setPlayersList(new ArrayList<>());
    gameMap.addGamePlayer(PLAYER_1);
    gameMap.addGamePlayer(PLAYER_2);
    playersList = gameMap.getPlayersList();
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    gameMap.placeAll();
    gameMap.setCurrentContext(Context.GAME_ATTACK);
    GameMap.setCurrentPlayerIndex(0);
    countries = gameMap.getCountries();
  }

  /** Test to check whether the armies are placed in valid countries */
  @Test
  void placeArmy() {
    Country sourceCountry = gameMap.getCountries().get(INDIA);
    sourceCountry.setOwnerName(PLAYER_1);
    gameMap.placeArmy(INDIA, 3);
    int actualArmies = sourceCountry.getNumberOfArmies();
    reason = "The country should have 3 armies";
    assertEquals(reason, 3, actualArmies);
  }

  /** Test to check all the countries are placed */
  @Test
  void placeAll() {
    boolean result = gameMap.placeAll();
    reason = "Armies should be placed for the players";
    assertTrue(reason, result);
  }
}
