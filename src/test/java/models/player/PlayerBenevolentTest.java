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

import static org.junit.Assert.assertEquals;

/**
 * test class to check the functionality of PlayerCheater.java {@link PlayerBenevolent}
 *
 * @author Siddharth Singh
 */
public class PlayerBenevolentTest {
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
    gameMap.addGamePlayer(PLAYER_1, "benevolent");
    gameMap.addGamePlayer(PLAYER_2, "human");
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    GameMap.setCurrentPlayerIndex(0);
  }

  /** check if player reinforces the weakest country that belongs to Benevolent Player */
  @Test
  public void reinforce() {
    Player player_1 = gameMap.getCurrentPlayer();
    ArrayList<Country> countries = new ArrayList<>(gameMap.getCountries().values());
    countries.stream()
        .filter(country -> country.getContinent().equals("Asia"))
        .forEach(country -> country.setOwnerName(PLAYER_1));
    countries.stream()
        .filter(country -> country.getContinent().equals("Africa"))
        .forEach(country -> country.setOwnerName(PLAYER_2));
    Optional<Country> weakestCountry =
        countries.stream()
            .min(Comparator.comparing(Country::getNumberOfArmies))
            .filter(c -> c.getOwnerName().equals(PLAYER_1));
    int originalArmyCount = weakestCountry.get().getNumberOfArmies();
    int numOfArmiesToReinforce = Player.calculateReinforcements(gameMap);
    player_1
        .getStrategy()
        .reinforce(gameMap, weakestCountry.get().getName(), numOfArmiesToReinforce);
    reason = "Benevolent player should reinforce the weakest country only";
    assertEquals(
        reason,
        originalArmyCount + numOfArmiesToReinforce,
        weakestCountry.get().getNumberOfArmies());
  }

  /** check if player fortifies the weakest country that belongs to Benevolent Player */
  @Test
  public void fortify() {
    Player player_2 = gameMap.getCurrentPlayer();
    ArrayList<Country> countries = new ArrayList<>(gameMap.getCountries().values());
    countries.stream()
        .filter(country -> country.getContinent().equals("Asia"))
        .forEach(country -> country.setOwnerName(PLAYER_1));
    countries.stream()
        .filter(country -> country.getContinent().equals("Africa"))
        .forEach(country -> country.setOwnerName(PLAYER_2));
    Optional<Country> weakestCountry =
        countries.stream()
            .min(Comparator.comparing(Country::getNumberOfArmies))
            .filter(c -> c.getOwnerName().equals(PLAYER_1));
    int originalArmy = weakestCountry.get().getNumberOfArmies();
    Optional<Country> strongestCountry =
        countries.stream()
            .max(Comparator.comparing(Country::getNumberOfArmies))
            .filter(c -> c.getOwnerName().equals(PLAYER_1));
    strongestCountry.get().setNumberOfArmies(10);
    player_2
        .getStrategy()
        .fortify(
            gameMap, weakestCountry.get().getName(), weakestCountry.get().getName(), originalArmy);
    int fortifyArmies = weakestCountry.get().getNumberOfArmies();
    reason = "Benevolent player should fortify with 5 armies";
    assertEquals(reason, 5, fortifyArmies);
  }
}
