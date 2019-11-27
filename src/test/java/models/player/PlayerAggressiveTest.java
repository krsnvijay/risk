package models.player;

import models.Context;
import models.Country;
import models.GameMap;
import org.junit.Before;
import org.junit.Test;
import utils.DominationMapParser;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * test class to check the functionality of PlayerAggresive.java {@link PlayerAggressive}
 *
 * @author Siddharth Singh
 */
public class PlayerAggressiveTest {
  public static final String PLAYER_1 = "Player1";
  public static final String PLAYER_2 = "Player2";
  /** reason for failure */
  String reason;

  /** store game state */
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
    gameMap.addGamePlayer(PLAYER_1, "aggressive");
    gameMap.addGamePlayer(PLAYER_2, "benevolent");
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    GameMap.setCurrentPlayerIndex(0);
  }

  /** check if player reinforces the strongest country that belongs to Aggressive Player */
  @Test
  public void reinforce() {
    Player player_1 = gameMap.getCurrentPlayer();
    ArrayList<Country> countries = new ArrayList<>(gameMap.getCountries().values());
    countries.stream()
        .filter(c -> c.getContinent().equals("Asia"))
        .forEach(c -> c.setOwnerName(PLAYER_1));
    countries.stream()
        .filter(c -> c.getContinent().equals("Africa"))
        .forEach(c -> c.setOwnerName(PLAYER_2));
    Optional<Country> strongestCountry =
        countries.stream()
            .max(Comparator.comparing(Country::getNumberOfArmies))
            .filter(c -> c.getOwnerName().equals(PLAYER_1));
    int originalArmyCount = strongestCountry.get().getNumberOfArmies();
    int numOfArmiesToReinforce = Player.calculateReinforcements(gameMap);
    player_1.getStrategy().setNumberOfArmies(numOfArmiesToReinforce);
    player_1
        .getStrategy()
        .reinforce(gameMap, strongestCountry.get().getName(), numOfArmiesToReinforce);
    reason = "Aggressive player should reinforce the strongest country only";
    assertEquals(
        reason,originalArmyCount + numOfArmiesToReinforce, strongestCountry.get().getNumberOfArmies());
  }

  /** check if player fortifies from the strongest country that belongs to Aggressive Player */
  @Test
  public void fortify() {
    ArrayList<Country> countries = new ArrayList<>(gameMap.getCountries().values());
    Player player_1 = gameMap.getCurrentPlayer();
    countries.stream()
        .filter(c -> c.getName().equals("Egypt"))
        .forEach(c -> c.setOwnerName(PLAYER_1));
    countries.stream()
        .filter(c -> c.getName().equals("India"))
        .forEach(c -> c.setOwnerName(PLAYER_1));
    ArrayList<Country> attackCountries =
        countries.stream()
            .sorted(Comparator.comparing(Country::getNumberOfArmies).reversed())
            .filter(c -> c.getOwnerName().equals(PLAYER_1))
            .collect(Collectors.toCollection(ArrayList::new));
    attackCountries.get(0).setNumberOfArmies(10);
    attackCountries.get(1).setNumberOfArmies(5);
    Country strongestCountry = attackCountries.get(0);
    Country strongerCountry = attackCountries.get(1);
    int expectedFortifyArmies =
        strongestCountry.getNumberOfArmies() + strongerCountry.getNumberOfArmies() - 1;
    player_1
        .getStrategy()
        .fortify(gameMap, strongestCountry.getName(), strongerCountry.getName(), 0);
    int actualFortifyArmies = strongestCountry.getNumberOfArmies();
      reason = "Aggressive player should fortify the strongest country only";
    assertEquals(reason, expectedFortifyArmies, actualFortifyArmies);
  }
}
