package models;

import models.player.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class GameState {
  /**
   * This maintains a list of players currently in the game.
   */
  public ArrayList<Player> playersList = new ArrayList<>();
  /**
   * This maintains a list of RISK cards in the deck.
   */
  public ArrayList<Card> deck = null;
  /**
   * Stores an adjacency list of all borders.
   */
  private Map<String, Set<String>> borders;
  /**
   * Stores a map of all continents.
   */
  private Map<String, Continent> continents;
  /**
   * Stores a map of all countries.
   */
  private Map<String, Country> countries;
  /**
   * The current phase of the game.
   */
  private Context currentContext;
  /**
   * Maintains whose turn it is (index).
   */
  private int currentPlayerIndex = 0;

  public Map<String, Set<String>> getBorders() {
    return borders;
  }

  public void setBorders(Map<String, Set<String>> borders) {
    this.borders = borders;
  }

  public Map<String, Continent> getContinents() {
    return continents;
  }

  public void setContinents(Map<String, Continent> continents) {
    this.continents = continents;
  }

  public Map<String, Country> getCountries() {
    return countries;
  }

  public void setCountries(Map<String, Country> countries) {
    this.countries = countries;
  }

  public Context getCurrentContext() {
    return currentContext;
  }

  public void setCurrentContext(Context currentContext) {
    this.currentContext = currentContext;
  }

  public int getCurrentPlayerIndex() {
    return this.currentPlayerIndex;
  }

  public void setCurrentPlayerIndex(int currentPlayerIndex) {
    this.currentPlayerIndex = currentPlayerIndex;
  }

  public ArrayList<Player> getPlayersList() {
    return playersList;
  }

  public void setPlayersList(ArrayList<Player> playersList) {
    this.playersList = playersList;
  }

  public ArrayList<Card> getDeck() {
    return deck;
  }

  public void setDeck(ArrayList<Card> deck) {
    this.deck = deck;
  }
}
