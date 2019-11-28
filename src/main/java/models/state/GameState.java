package models.state;

import models.Card;
import models.Context;
import models.Continent;
import models.Country;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * This class maintains the entire state of the game for serialization (saving/loading).
 *
 * @author Vijay
 * @version 1.0
 */
public class GameState {

  /** This maintains a list of players currently in the game. */
  private ArrayList<PlayerState> playersList = new ArrayList<>();

  /** This maintains a list of RISK cards in the deck. */
  private ArrayList<Card> deck = null;

  /** Stores an adjacency list of all borders. */
  private Map<String, Set<String>> borders;

  /** Stores a map of all continents. */
  private Map<String, Continent> continents;

  /** Stores a map of all countries. */
  private Map<String, Country> countries;

  /** The current phase of the game. */
  private Context currentContext;

  /** Maintains whose turn it is (index). */
  private int currentPlayerIndex = 0;

  /** Maintains the number of sets traded in game */
  private int numberOfTradedSet = 0;

  /** Number of armies traded in for each set */
  private int armiesTradedForSet = 0;

  /** Total cards introduced in the game */
  private int cardCount = 0;

  /**
   * Gets the list of players stored in the state.
   *
   * @return ArrayList of PlayerState objects.
   */
  public ArrayList<PlayerState> getPlayersList() {
    return playersList;
  }

  /**
   * Sets the players list.
   *
   * @param playersList the player list.
   */
  public void setPlayersList(ArrayList<PlayerState> playersList) {
    this.playersList = playersList;
  }

  /**
   * Gets the deck of Cards for the game.
   *
   * @return An ArrayList of Cards (the deck).
   */
  public ArrayList<Card> getDeck() {
    return deck;
  }

  /**
   * Sets the deck of cards.
   *
   * @param deck the deck as an ArrayList.
   */
  public void setDeck(ArrayList<Card> deck) {
    this.deck = deck;
  }

  /**
   * Gets the borders for the map.
   *
   * @return A Map object with the borders.
   */
  public Map<String, Set<String>> getBorders() {
    return borders;
  }

  /**
   * Sets the borders list for the map.
   *
   * @param borders A map of borders.
   */
  public void setBorders(Map<String, Set<String>> borders) {
    this.borders = borders;
  }

  /**
   * Gets the continents in the map.
   *
   * @return A Map object with the continents.
   */
  public Map<String, Continent> getContinents() {
    return continents;
  }

  /**
   * Sets the continents for the map.
   *
   * @param continents A map of continents.
   */
  public void setContinents(Map<String, Continent> continents) {
    this.continents = continents;
  }

  /**
   * Gets the countries in the map.
   *
   * @return A Map object with the countries.
   */
  public Map<String, Country> getCountries() {
    return countries;
  }

  /**
   * Sets the countries for the map.
   *
   * @param countries A map of countries.
   */
  public void setCountries(Map<String, Country> countries) {
    this.countries = countries;
  }

  /**
   * Gets the current context for the game.
   *
   * @return A Context enum object.
   */
  public Context getCurrentContext() {
    return currentContext;
  }

  /**
   * Sets the current game context.
   *
   * @param currentContext A Context object.
   */
  public void setCurrentContext(Context currentContext) {
    this.currentContext = currentContext;
  }

  /**
   * Gets the index for the current player in the playersList.
   *
   * @return An integer representing the current player index.
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * Sets the current player index.
   *
   * @param currentPlayerIndex An integer.
   */
  public void setCurrentPlayerIndex(int currentPlayerIndex) {
    this.currentPlayerIndex = currentPlayerIndex;
  }

  /**
   * Gets the number of card sets that have been traded.
   *
   * @return An integer representing the number of card sets that have been traded.
   */
  public int getNumberOfTradedSet() {
    return numberOfTradedSet;
  }

  /**
   * Sets the number of traded card sets.
   *
   * @param numberOfTradedSet An integer.
   */
  public void setNumberOfTradedSet(int numberOfTradedSet) {
    this.numberOfTradedSet = numberOfTradedSet;
  }

  /**
   * Gets the number of armies that have been traded for a card set.
   *
   * @return An integer representing the number of armies traded.
   */
  public int getArmiesTradedForSet() {
    return armiesTradedForSet;
  }

  /**
   * Sets number of armies traded for sets.
   *
   * @param armiesTradedForSet An integer.
   */
  public void setArmiesTradedForSet(int armiesTradedForSet) {
    this.armiesTradedForSet = armiesTradedForSet;
  }

  /**
   * Gets the number of cards.
   *
   * @return An integer representing the number of cards.
   */
  public int getCardCount() {
    return cardCount;
  }

  /**
   * Sets the count of cards.
   *
   * @param cardCount An integer.
   */
  public void setCardCount(int cardCount) {
    this.cardCount = cardCount;
  }
}
