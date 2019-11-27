package models.player;

import models.Context;
import models.Country;
import models.GameMap;
import models.player.Player;
import models.player.PlayerRandom;
import org.junit.Before;
import org.junit.Test;
import utils.DominationMapParser;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * test class to check the functionalities of PlayerRandom.java {@link Player}
 */
public class PlayerRandomTest {

  public static final String PLAYER_1 = "Player1";
  public static final String PLAYER_2 = "Player2";
  public static final String INDIA = "India";
  public static final String CHINA = "China";

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
    File riskMap = new File("src/test/resources/simple.map");
    MapParser mapParser = new DominationMapParser();
    gameMap = mapParser.loadMap(riskMap.getPath());
    reason = "";
    gameMap.setPlayersList(new ArrayList<>());
    gameMap.addGamePlayer(PLAYER_1, "random");
    gameMap.addGamePlayer(PLAYER_2, "random");
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    gameMap.placeAll();
    GameMap.setCurrentPlayerIndex(0);
    countries = gameMap.getCountries();

  }

  /** check if player reinforces any random country that belongs to the Random Player */
  @Test
  public void reinforceTest() {
    Player player_1 = gameMap.getCurrentPlayer();
    Random randomGenerator = new Random(42);
    ArrayList<Country> countries = Player.getCountriesByOwnership(PLAYER_1, gameMap);
    Country reinforcedCountry = countries.get(randomGenerator.nextInt(countries.size()));
    int originalArmyCount = reinforcedCountry.getNumberOfArmies();
    int numOfArmiesToReinforce = Player.calculateReinforcements(gameMap);
    GameMap.setRandomGenerator(42);
    player_1.getStrategy().reinforce(gameMap, reinforcedCountry.getName(), numOfArmiesToReinforce);
    assertEquals(originalArmyCount + numOfArmiesToReinforce, reinforcedCountry.getNumberOfArmies());
    randomGenerator.setSeed(23);
    reinforcedCountry = countries.get(randomGenerator.nextInt(countries.size()));
    originalArmyCount = reinforcedCountry.getNumberOfArmies();
    numOfArmiesToReinforce = Player.calculateReinforcements(gameMap);
    GameMap.setRandomGenerator(23);
    player_1.getStrategy().reinforce(gameMap, reinforcedCountry.getName(), numOfArmiesToReinforce);
    assertEquals(originalArmyCount + numOfArmiesToReinforce, reinforcedCountry.getNumberOfArmies());
  }

  /** check if player attacks from any random country that belongs to the Random Player */
  @Test
  public void attackTest() {
    PlayerRandom player_1 = new PlayerRandom(PLAYER_1);
    Random randomGenerator = new Random(42);
    GameMap.setRandomGenerator(42);
    String result = player_1.randomAttack(gameMap);
    assertThat(result, not(containsString("-noattack")));
  }


  /** check if player fortifies any random country that belongs to the Random Player */
  @Test
  public void fortifyTest() {
    PlayerRandom player_1 = new PlayerRandom(PLAYER_1);
    Random randomGenerator = new Random(40);
    String result = player_1.randomFortify(gameMap);
    assertThat(result, containsString("none"));
  }
}
