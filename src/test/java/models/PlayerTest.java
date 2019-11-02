package models;

import org.junit.Before;
import org.junit.Test;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;

/**
 * test class to check the functionalities of Player.java {@link Player}
 */
public class PlayerTest {

  /** list of playes */
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
    gameMap = MapParser.loadMap(riskMap.getPath());
    reason = "";
    player1 = new Player("Player1");
    player2 = new Player("Player2");
    playersList.add(player1);
    playersList.add(player2);
    countries = new ArrayList<>(gameMap.getCountries().values());
  }

  /** check if countries are equally divided among the players */
  @Test
  public void getCountriesByOwnership() {
    playerName = player1.getPlayerName();
    gameMap.setCountries(gameMap.populateCountries(playersList));
    ArrayList<Country> noOfCountries = new ArrayList<Country>();
    noOfCountries = Player.getCountriesByOwnership(playerName, gameMap);
    int ownedCountries = Player.getCountriesByOwnership("Player2", gameMap).size();
    reason = "Number of countries should be zero";
    assertEquals(reason, 21, ownedCountries);
  }

  /** calculate whether the Reinforcement armies are calculated correctly */
  @Test
  public void calculateReinforcements() {
    gameMap.setCountries(gameMap.populateCountries(playersList));
    int ownedCountries = Player.getCountriesByOwnership("Player1", gameMap).size();
    int ownedContinents = Player.getBonusArmiesIfPlayerOwnsContinents("Player1", gameMap);
    int expectedReinforcementArmies = ownedContinents + (ownedCountries / 3);
    int actualReinforcementArmies = player1.calculateReinforcements(gameMap);
    reason = "Number of reinforcement armies should be " + expectedReinforcementArmies;
    assertEquals(reason, expectedReinforcementArmies, actualReinforcementArmies);
  }

  /** calculate whether the bonus armies for players who own a continent is calculated correctly */
  @Test
  public void checkBonusArmiesForContinent() {
    // Arrange
    countries.stream()
        .filter(country -> country.getContinent().equals("Asia"))
        .forEach(country -> country.setOwnerName("Player1"));
    countries.stream()
        .filter(country -> country.getContinent().equals("Africa"))
        .forEach(country -> country.setOwnerName("Player2"));
    countries.stream()
        .filter(country -> !country.getContinent().equals("Africa") && !country.getContinent()
            .equals("Asia"))
        .forEach(country -> country.setOwnerName("Player3"));
    int bonusArmyAsia = gameMap.getContinents().get("Asia").getValue();
    int bonusArmyAfrica = gameMap.getContinents().get("Africa").getValue();

    // Act
    int actualBonusArmiesAsia = Player.getBonusArmiesIfPlayerOwnsContinents("Player1", gameMap);
    int actualBonusArmiesAfrica = Player.getBonusArmiesIfPlayerOwnsContinents("Player2", gameMap);

    // Assert
    reason = "Numer of bonus armies expected is " + actualBonusArmiesAsia;
    assertEquals(reason, bonusArmyAsia, actualBonusArmiesAsia);

    reason = "Numer of bonus armies expected is " + actualBonusArmiesAfrica;
    assertEquals(reason, bonusArmyAfrica, actualBonusArmiesAfrica);
  }

  /**
   * another variant of populateCountries to manage the context in this test class
   *
   * @param playerList list of players
   * @param countries list of countriess
   * @return Map of country name and country object
   */
  public Map<String, Country> populateCountriesVariant(
      ArrayList<Player> playerList, ArrayList<Country> countries) {
    countries = countries.stream().skip(9).collect(Collectors.toCollection(ArrayList::new));
    Collections.shuffle(countries);
    int countrySize = countries.size();
    for (int i = 0; i < countrySize; i++) {
      if (i < 14) {
        countries.get(i).setOwnerName("Player2");
      } else {
        countries.get(i).setOwnerName("Player1");
      }
    }
    return countries.stream().collect(toMap(Country::getName, c -> c));
  }
}
