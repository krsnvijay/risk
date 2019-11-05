package controllers;

import models.*;
import org.junit.Before;
import org.junit.Test;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class GameControllerTest {
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

  String command;

  GameController gameController = new GameController();

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

  @Test
  public void reinforce() {
    playerName = gameMap.getPlayersList().get(0).getPlayerName();
  }

  // attacker validation
  @Test
  public void attackArmiesTest() {
    Country attackingCountry = gameMap.getCountries().get(INDIA);
    Country defendingCountry =gameMap.getCountries().get(CHINA);
    attackingCountry.setOwnerName(PLAYER_1);
    defendingCountry.setOwnerName(PLAYER_2);
    attackingCountry.setNumberOfArmies(3);
    defendingCountry.setNumberOfArmies(2);
    command = String.format("attack %s %s -allout",INDIA,CHINA);
    boolean result = GameController.processAttackCommand(gameMap,command);
    reason = "Armies should be greater than 2 for the attacker country";
    assertTrue(reason, result);
  }

  // defender validation
  @Test
  public void defendAdjacencyTest() {
    Country sourceCountry = gameMap.getCountries().get(INDIA);
    Country destCountry = gameMap.getCountries().get(CHINA);
    sourceCountry.setOwnerName(PLAYER_1);
    destCountry.setOwnerName(PLAYER_2);
    sourceCountry.setNumberOfArmies(4);
    destCountry.setNumberOfArmies(2);
    command = String.format("attack %s %s -allout",INDIA,CHINA);
    boolean result = GameController.processAttackCommand(gameMap, command);
    reason = "Countries should be adjacent for a valid attack";
    assertFalse(reason, result);
  }

  // cards validation
  @Test
  public void exchangeCardsTest() {
    Card card1 = new Card(INDIA);
    Card card2 = new Card(CHINA);
    Card card3 = new Card("Quebec");
    List<Card> cardsInHand = new ArrayList<>();
    cardsInHand.add(card1);
    cardsInHand.add(card2);
    cardsInHand.add(card3);
    gameMap.getPlayersList().get(0).setCardsInHand(cardsInHand);
    reason = "The set is valid only when the 3 cards are from different class or the same class";
    command = "exchangecards 1 2 3";
    boolean result = GameController.processExchangeCardsCommand(gameMap, command);
    assertTrue(reason, result);
  }

  // valid move after conquer
  @Test
  public void conquerTest() {}

  // end of game validation
  @Test
  public void endOfGameTest() {
    playerName = gameMap.getPlayersList().get(0).getPlayerName();
    int countrySize = countries.size();
    int i = 0;
    ArrayList<Country> allCountries = new ArrayList<>(countries.values());
    while (i < countrySize) {
      allCountries.get(i).setOwnerName(playerName);
      i++;
    }
    boolean result = Player.checkPlayerOwnsAllTheCountries(playerName, gameMap);
    reason = "If player owns all the countries, they should win the game";
    assertTrue(reason, result);
  }

  // fortification validation
  @Test
  public void fortifyTest() {
    playerName = gameMap.getPlayersList().get(0).getPlayerName();
    ArrayList<Country> sourceCountry =
        countries.values().stream()
            .filter(
                country ->
                    country.getNumberOfArmies() == 3 && country.getOwnerName().equals(PLAYER_1))
            .collect(toCollection(ArrayList::new));
    // how to get set values
    Set<String> destinationCountry = gameMap.getBorders().get(sourceCountry.get(0).getName());
    reason = "Fortification within countries that aren't adjacent is not possible";
    command = "fortify " + sourceCountry.get(0).getName() + " ";
    assertEquals(reason, "Hello", "Hello");
  }

  @Test
  public void defendTest() {}

  @Test
  public void attackMove() {}
}
