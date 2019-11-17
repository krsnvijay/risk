package controllers;

import models.Context;
import models.Country;
import models.GameMap;
import org.junit.Before;
import org.junit.Test;
import utils.DominationMapParser;
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
   * reason for failure
   */
  String reason;

  /** list of countries */
  Map<String, Country> countries;

  /** store game state */
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
    gameMap.addGamePlayer(PLAYER_1);
    gameMap.addGamePlayer(PLAYER_2);
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    gameMap.placeAll();
    gameMap.setCurrentContext(Context.GAME_ATTACK);
    GameMap.setCurrentPlayerIndex(0);
    countries = gameMap.getCountries();
  }

  /** validate calculateMaxDice for Attacker */
  @Test
  public void calculateMaxDiceForAttacker() {

    Country India = gameMap.getCountries().get(INDIA);
    India.setNumberOfArmies(4);
    India.setOwnerName(PLAYER_1);
    Country China = gameMap.getCountries().get(CHINA);
    China.setNumberOfArmies(3);
    China.setOwnerName(PLAYER_2);
    BattleController battleController =
        new BattleController(gameMap, "attack India China 3 -allout");
    assertEquals(3, battleController.calculateMaxDiceForAttacker());
    India.setNumberOfArmies(2);
    assertEquals(1, battleController.calculateMaxDiceForAttacker());
    India.setNumberOfArmies(1);
    assertEquals(0, battleController.calculateMaxDiceForAttacker());
  }

  /** validate calculateMaxDice for Defender */
  @Test
  public void calculateMaxDiceForDefender() {
    Country India = gameMap.getCountries().get(INDIA);
    India.setNumberOfArmies(4);
    India.setOwnerName(PLAYER_1);
    Country China = gameMap.getCountries().get(CHINA);
    China.setNumberOfArmies(3);
    China.setOwnerName(PLAYER_2);
    BattleController battleController =
        new BattleController(gameMap, "attack India China 3 -allout");
    assertEquals(2, battleController.calculateMaxDiceForDefender());
    China.setNumberOfArmies(1);
    assertEquals(1, battleController.calculateMaxDiceForDefender());
    China.setNumberOfArmies(0);
    assertEquals(0, battleController.calculateMaxDiceForDefender());
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
    BattleController battleController =
        new BattleController(gameMap, "attack India China 3 -allout");
    assertEquals(
        battleController.calculateMaxDiceForAttacker(),
        battleController.getNumOfDiceFromAttacker());
    battleController = new BattleController(gameMap, "attack India China 3");
    assertEquals(3, battleController.getNumOfDiceFromAttacker());
  }

  /** validate getNumberofDice for defender */
  @Test
  public void getNumberOfDiceDefender() {
    Country India = gameMap.getCountries().get(INDIA);
    India.setNumberOfArmies(4);
    India.setOwnerName(PLAYER_1);
    Country China = gameMap.getCountries().get(CHINA);
    China.setNumberOfArmies(3);
    China.setOwnerName(PLAYER_2);

    BattleController battleController = new BattleController(gameMap, "attack India China 3");
    // Stop input loop and manually set the numOfDiceDefender
    battleController.setNoInputEnabled(true);
    battleController.setNumOfDiceDefender(1);
    assertEquals(1, battleController.getNumOfDiceFromDefender());

    battleController = new BattleController(gameMap, "attack India China -allout");
    assertEquals(
        battleController.calculateMaxDiceForDefender(),
        battleController.getNumOfDiceFromDefender());
  }

  /** validate successful attack */
  @Test
  public void successfulAttack() {
    Country India = gameMap.getCountries().get(INDIA);
    India.setNumberOfArmies(4);
    India.setOwnerName(PLAYER_1);
    Country China = gameMap.getCountries().get(CHINA);
    China.setNumberOfArmies(3);
    China.setOwnerName(PLAYER_2);
    BattleController battleController = new BattleController(gameMap, "attack India China 3");
    battleController.successfulAttack();
    assertEquals(2, China.getNumberOfArmies());
  }

  /** validate successful defense */
  @Test
  public void successfulDefense() {
    Country India = gameMap.getCountries().get(INDIA);
    India.setNumberOfArmies(4);
    India.setOwnerName(PLAYER_1);
    Country China = gameMap.getCountries().get(CHINA);
    China.setNumberOfArmies(3);
    China.setOwnerName(PLAYER_2);
    BattleController battleController = new BattleController(gameMap, "attack India China 3");
    battleController.successfulDefence();
    assertEquals(3, India.getNumberOfArmies());
  }
}
