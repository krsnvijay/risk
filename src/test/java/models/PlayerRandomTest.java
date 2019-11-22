package models;

import models.player.Player;
import org.junit.Before;
import org.junit.Test;
import utils.DominationMapParser;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

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
    File riskMap = new File("src/test/resources/risk.map");
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

}
