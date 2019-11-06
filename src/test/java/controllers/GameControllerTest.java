package controllers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Card;
import models.Context;
import models.Country;
import models.GameMap;
import models.Player;
import org.junit.Before;
import org.junit.Test;
import utils.MapParser;


/**
 * test class to check the functionalities of GameController.java
 *
 * @see controllers.GameController
 * @author Siddharth Singh
 */
public class GameControllerTest {
  public static final String PLAYER_1 = "Player1";
  public static final String PLAYER_2 = "Player2";
  public static final String INDIA = "India";
  public static final String CHINA = "China";
  public static final String QUEBEC = "Quebec";
  /** list of players */
  private static ArrayList<Player> playersList = new ArrayList<>();
  /** player name */
  String playerName;
  /** reason for failure */
  String reason;
  /** commands by the player */
  String command;

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
    GameMap.setCurrentPlayerIndex(0);
    countries = gameMap.getCountries();
  }


  /** Test to check whether the reinforced army is added to the country*/
/*  @Test
  public void reinforce() {
    Country sourceCountry = gameMap.getCountries().get(INDIA);
    sourceCountry.setOwnerName(PLAYER_1);
    sourceCountry.setNumberOfArmies(5);
    boolean result = gameMap.getPlayersList().get(0).reinforce(gameMap, CHINA, 2);
    int numberOfArmies = sourceCountry.getNumberOfArmies();
    reason = "The total armies should be 4";
    assertTrue(reason, result);
  }
  */

  /** Test to check whether the attacking country has more than 2 armies*/
  @Test
  public void attackArmiesTest() {
    Country attackingCountry = gameMap.getCountries().get(INDIA);
    Country defendingCountry = gameMap.getCountries().get(CHINA);
    attackingCountry.setOwnerName(PLAYER_1);
    defendingCountry.setOwnerName(PLAYER_2);
    attackingCountry.setNumberOfArmies(1);
    defendingCountry.setNumberOfArmies(3);
    command = String.format("attack %s %s -allout", INDIA, CHINA);
    boolean result = GameController.processAttackCommand(gameMap, command);
    reason = "Armies should be greater than 2 for the attacker country";
    assertFalse(reason, result);
  }

  /** Test to check whether the attacking countries are adjacent*/
  @Test
  public void defendAdjacencyTest() {
    Country sourceCountry = gameMap.getCountries().get(INDIA);
    Country destCountry = gameMap.getCountries().get(QUEBEC);
    sourceCountry.setOwnerName(PLAYER_1);
    destCountry.setOwnerName(PLAYER_2);
    sourceCountry.setNumberOfArmies(4);
    destCountry.setNumberOfArmies(2);
    command = String.format("attack %s %s -allout", INDIA, QUEBEC);
    boolean result = GameController.processAttackCommand(gameMap, command);
    reason = "Countries should be adjacent for a valid attack";
    assertFalse(reason, result);
  }

  /** Test to check whether the card set is valid*/
  @Test
  public void exchangeCardsTest() {
    Card card1 = new Card(INDIA);
    Card card2 = new Card(CHINA);
    Card card3 = new Card(QUEBEC);
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

  /** Test to check whether the player conquers a valid country*/
  @Test
  public void conquerTest() {

  }

  /** Test to check if a player won the game */
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

  /** Test to check whether fortification happens only within adjacent countries */
  @Test
  public void fortifyTest() {
    Country sourceCountry = gameMap.getCountries().get(INDIA);
    Country destCountry = gameMap.getCountries().get(QUEBEC);
    sourceCountry.setOwnerName(PLAYER_1);
    destCountry.setOwnerName(PLAYER_1);
    sourceCountry.setNumberOfArmies(4);
    destCountry.setNumberOfArmies(2);
    command = String.format("fortify %s %s 2", INDIA, QUEBEC);
    boolean result = GameController.processFortifyCommand(gameMap, command);
    reason = "Fortification within countries that aren't adjacent is not possible";
    assertFalse(reason, result);
  }
}
