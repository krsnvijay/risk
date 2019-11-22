package models;

import models.player.Player;
import org.junit.Before;
import org.junit.Test;
import utils.DominationMapParser;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;

/** test class to check the functionalities of PlayerHuman.java {@link Player} */
public class PlayerTest {

  public static final String PLAYER_1 = "Player1";
  public static final String PLAYER_2 = "Player2";

  /** player name */
  String playerName;
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
    File riskMap = new File("src/test/resources/risk.map");
    MapParser mapParser = new DominationMapParser();
    gameMap = mapParser.loadMap(riskMap.getPath());
    reason = "";
    gameMap.setPlayersList(new ArrayList<>());
    gameMap.addGamePlayer(PLAYER_1, "human");
    gameMap.addGamePlayer(PLAYER_2, "human");
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    gameMap.placeAll();
    GameMap.setCurrentPlayerIndex(0);
  }

  /** check if countries are equally divided among the players */
  @Test
  public void getCountriesByOwnership() {

    int ownedCountries = Player.getCountriesByOwnership(PLAYER_2, gameMap).size();
    reason = "Number of countries should be 21";
    assertEquals(reason, 21, ownedCountries);
  }

  /** calculate whether the Reinforcement armies are calculated correctly */
  @Test
  public void calculateReinforcements() {
    int ownedCountries = Player.getCountriesByOwnership(PLAYER_1, gameMap).size();
    int ownedContinents = Player.getBonusArmiesIfPlayerOwnsContinents(PLAYER_1, gameMap);
    int expectedReinforcementArmies = ownedContinents + (ownedCountries / 3);
    Player player_Human_1 = gameMap.getPlayersList().get(0);
    int actualReinforcementArmies = player_Human_1.calculateReinforcements(gameMap);
    reason = "Number of reinforcement armies should be " + expectedReinforcementArmies;
    assertEquals(reason, expectedReinforcementArmies, actualReinforcementArmies);
  }

  /** calculate whether the bonus armies for players who own a continent is calculated correctly */
  @Test
  public void checkBonusArmiesForContinent() {
    // Arrange
    Collection<Country> countries = gameMap.getCountries().values();
    countries.stream()
        .filter(country -> country.getContinent().equals("Asia"))
        .forEach(country -> country.setOwnerName(PLAYER_1));
    countries.stream()
        .filter(country -> country.getContinent().equals("Africa"))
        .forEach(country -> country.setOwnerName(PLAYER_2));
    countries.stream()
        .filter(
            country ->
                !country.getContinent().equals("Africa") && !country.getContinent().equals("Asia"))
        .forEach(country -> country.setOwnerName("Player3"));
    int bonusArmyAsia = gameMap.getContinents().get("Asia").getValue();
    int bonusArmyAfrica = gameMap.getContinents().get("Africa").getValue();

    // Act
    int actualBonusArmiesAsia = Player.getBonusArmiesIfPlayerOwnsContinents(PLAYER_1, gameMap);
    int actualBonusArmiesAfrica = Player.getBonusArmiesIfPlayerOwnsContinents(PLAYER_2, gameMap);

    // Assert
    reason = "Number of bonus armies expected is " + actualBonusArmiesAsia;
    assertEquals(reason, bonusArmyAsia, actualBonusArmiesAsia);

    reason = "Number of bonus armies expected is " + actualBonusArmiesAfrica;
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
        countries.get(i).setOwnerName(PLAYER_2);
      } else {
        countries.get(i).setOwnerName(PLAYER_1);
      }
    }
    return countries.stream().collect(toMap(Country::getName, c -> c));
  }
}
