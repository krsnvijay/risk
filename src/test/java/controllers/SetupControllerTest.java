package controllers;

import models.Context;
import models.Country;
import models.GameMap;
import models.player.Player;
import org.junit.Before;
import org.junit.Test;
import utils.DominationMapParser;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertTrue;


/**
 * test class to check the functionalities of SetupController.java
 *
 * @see controllers.SetupController
 * @author Sabari Venkadesh
 */
public class SetupControllerTest {
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
  public void setUp() throws Exception {
    File riskMap = new File("src/test/resources/risk.map");
    MapParser mapParser = new DominationMapParser();
    gameMap = mapParser.loadMap(riskMap.getPath());
    reason = "";
    gameMap.setPlayersList(new ArrayList<>());
    gameMap.addGamePlayer(PLAYER_1, "human");
    gameMap.addGamePlayer(PLAYER_2, "human");
    playersList = gameMap.getPlayersList();
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    gameMap.placeAll();
    gameMap.setCurrentContext(Context.GAME_ATTACK);
    GameMap.setCurrentPlayerIndex(0);
    countries = gameMap.getCountries();
  }

  /** Test to check whether the countries are populated*/
  @Test
  public void populateCountries() {
    ArrayList<Player> activePlayerHumen = gameMap.getPlayersList();
    ArrayList<Country> populatedCountries = new ArrayList<>(gameMap.populateCountries(activePlayerHumen).values());
    boolean result = true;
    for (Country populatedCountry : populatedCountries) {
      if (populatedCountry.getOwnerName() == null) {
        result = false;
        break;
      }
    }
    reason = "All the countries should have an owner";
    assertTrue(reason, result);
  }
}
