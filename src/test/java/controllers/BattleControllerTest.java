package controllers;

import models.Context;
import models.Country;
import models.GameMap;
import models.Player;
import org.junit.Before;
import org.junit.Test;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * test class to check the functionalities of BattleController.java
 *
 * @see controllers.BattleController
 * @author Sabari Venkadesh
 */

public class BattleControllerTest {

  public static final String PLAYER_1 = "Player1";
  public static final String PLAYER_2 = "Player2";
  public static final String INDIA = "India";
  public static final String CHINA = "China";
  /**
   * list of players
   */
  private static ArrayList<Player> playersList = new ArrayList<>();
  /**
   * player name
   */
  String playerName;
  /**
   * reason for failure
   */
  String reason;

  String command;

  GameController gameController = new GameController();

  /**
   * list of countries
   */
  Map<String, Country> countries;

  /**
   * store game state
   */
  private GameMap gameMap;
  /**
   * store player 1
   */
  private Player player1;
  /**
   * store player 2
   */
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
    command = "";
    gameMap.addGamePlayer(PLAYER_1);
    gameMap.addGamePlayer(PLAYER_2);
    playersList = gameMap.getPlayersList();
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    gameMap.placeAll();
    gameMap.setCurrentContext(Context.GAME_ATTACK);
    countries = gameMap.getCountries();
  }

  /**
   * validate calculateMaxDice for Attacker
   */

  @Test
  public void calculateMaxDiceForAttacker() {

    Country India = gameMap.getCountries().get(INDIA);
    India.setNumberOfArmies(4);
    India.setOwnerName(PLAYER_1);
    Country China = gameMap.getCountries().get(CHINA);
    China.setNumberOfArmies(3);
    China.setOwnerName(PLAYER_2);
    BattleController battleController = new BattleController(gameMap, "attack India China 3 -allout");
    assertEquals(battleController.calculateMaxDiceForAttacker(), 3);
    India.setNumberOfArmies(2);
    assertEquals(battleController.calculateMaxDiceForAttacker(), 1);
    India.setNumberOfArmies(1);
    assertEquals(battleController.calculateMaxDiceForAttacker(), 0);


  }

  /**validate calculateMaxDice for Defender*/

  @Test
  public void calculateMaxDiceForDefender() {
    Country India = gameMap.getCountries().get(INDIA);
    India.setNumberOfArmies(4);
    India.setOwnerName(PLAYER_1);
    Country China = gameMap.getCountries().get(CHINA);
    China.setNumberOfArmies(3);
    China.setOwnerName(PLAYER_2);
    BattleController battleController = new BattleController(gameMap, "attack India China 3 -allout");
    assertEquals(battleController.calculateMaxDiceForDefender(), 2);
    China.setNumberOfArmies(1);
    assertEquals(battleController.calculateMaxDiceForDefender(), 1);
    China.setNumberOfArmies(0);
    assertEquals(battleController.calculateMaxDiceForDefender(), 0);
  }

  /** validate getNumberofDice for attacker */

  @Test
  public void getNumberOfDiceAttacker() {
    Country India = gameMap.getCountries().get(INDIA);
    India.setNumberOfArmies(4);
    India.setOwnerName(PLAYER_1);
    Country China = gameMap.getCountries().get(CHINA);
    China.setNumberOfArmies(3);
    China.setOwnerName(PLAYER_2);
    BattleController battleController = new BattleController(gameMap, "attack India China 3 -allout");
    assertEquals(battleController.getNumOfDiceFromAttacker(), battleController.calculateMaxDiceForAttacker());
    BattleController battleController2 = new BattleController(gameMap, "attack India China 3");
    assertEquals(battleController2.getNumOfDiceFromAttacker(), 3);
  }
}
