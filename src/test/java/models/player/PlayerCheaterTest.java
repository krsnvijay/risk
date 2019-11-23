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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * test class to check the functionality of PlayerCheater.java {@link PlayerCheater}
 *
 * @author Siddharth Singh
 */
public class PlayerCheaterTest {
  public static final String PLAYER_1 = "Player1";
  public static final String PLAYER_2 = "Player2";
  /** reason for failure */
  String reason;
  /** store game state */
  private GameMap gameMap;
  /** Stores the number of armies a player has. */
  private int numberOfArmies;

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
    gameMap.addGamePlayer(PLAYER_1, "cheater");
    gameMap.addGamePlayer(PLAYER_2, "cheater");
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    GameMap.setCurrentPlayerIndex(0);
  }
  /** check if attack will capture all neighbors of countries that belong to the cheater */
  @Test
  public void attack() {
    String command = "";
    Player player_1 = gameMap.getCurrentPlayer();
    player_1.getStrategy().attack(gameMap, command);
    int countriesOwned = Player.getCountriesByOwnership(PLAYER_1, gameMap).size();
    reason = "Cheater should own all the countries in one move";
    assertEquals(reason, 4, countriesOwned);
  }

  /** check if reinforce doubles the armies for countries that belong to the cheater */
  @Test
  public void reinforce() {
    gameMap.placeAll();
    String command = "";
    Player player_1 = gameMap.getCurrentPlayer();
    int numOfArmiesToReinforce = Player.calculateReinforcements(gameMap);
    String reinforcedCountry = Player.getCountriesByOwnership(PLAYER_1, gameMap).get(0).getName();
    ArrayList<Country> countries = Player.getCountriesByOwnership(PLAYER_1, gameMap);
    int cheaterArmy = countries.get(0).getNumberOfArmies();
    player_1.getStrategy().reinforce(gameMap, reinforcedCountry, numOfArmiesToReinforce);
    reason = "Cheater's armies should double after reinforce";
    assertEquals(reason, cheaterArmy, countries.get(0).getNumberOfArmies());
  }

  /** check if fortify doubles the armies for countries that doesn't belong to the cheater */
  @Test
  public void fortify() {
    String command = "";
    Player player_1 = gameMap.getCurrentPlayer();
    ArrayList<Country> countries = Player.getCountriesByOwnership(PLAYER_1, gameMap);
    countries.stream()
        .filter(country -> country.getContinent().equals("Africa"))
        .forEach(country -> country.setOwnerName(PLAYER_2));
    countries.stream()
        .filter(country -> country.getContinent().equals("Asia"))
        .forEach(country -> country.setOwnerName(PLAYER_1));
    Map<String, Country> allCountries = gameMap.getCountries();
    int cheaterArmy = countries.get(0).getNumberOfArmies();

    ArrayList<Country> cheatCountry = new ArrayList<>();
    for (Country cheaterCountry : countries) {
      Set<String> borders = gameMap.getBorders().get(cheaterCountry.getName());
      for (String neighborName : borders) {
        Country neighbor = allCountries.get(neighborName);
        if (!neighbor.getOwnerName().equals(PLAYER_1)) {
          cheatCountry.add(cheaterCountry);
        }
      }
    }
    int expectedCheaterArmy = cheatCountry.get(0).getNumberOfArmies();
    System.out.println(cheatCountry.get(0).getName());
    player_1
        .getStrategy()
        .fortify(gameMap, cheatCountry.get(0).getName(), cheatCountry.get(1).getName(), cheaterArmy);
    ArrayList<Country> actualCheaterArmy =
        cheatCountry.stream()
            .filter(c -> c.getName().equals(cheatCountry.get(0).getName()))
            .collect(Collectors.toCollection(ArrayList::new));
    System.out.println(actualCheaterArmy.get(0).getName());
    assertEquals(reason, expectedCheaterArmy, actualCheaterArmy.get(0).getNumberOfArmies());
  }
}