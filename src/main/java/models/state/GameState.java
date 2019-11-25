package models.state;

import models.Card;
import models.Context;
import models.Continent;
import models.Country;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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

  public GameState() {
  }

  public ArrayList<PlayerState> getPlayersList() {
    return playersList;
  }

  public ArrayList<Card> getDeck() {
    return deck;
  }

  public Map<String, Set<String>> getBorders() {
    return borders;
  }

  public Map<String, Continent> getContinents() {
    return continents;
  }

  public Map<String, Country> getCountries() {
    return countries;
  }

  public Context getCurrentContext() {
    return currentContext;
  }

  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  public int getNumberOfTradedSet() {
    return numberOfTradedSet;
  }

  public int getArmiesTradedForSet() {
    return armiesTradedForSet;
  }

  public int getCardCount() {
    return cardCount;
  }

  public void setPlayersList(ArrayList<PlayerState> playersList) {
    this.playersList = playersList;
  }

  public void setDeck(ArrayList<Card> deck) {
    this.deck = deck;
  }

  public void setBorders(Map<String, Set<String>> borders) {
    this.borders = borders;
  }

  public void setContinents(Map<String, Continent> continents) {
    this.continents = continents;
  }

  public void setCountries(Map<String, Country> countries) {
    this.countries = countries;
  }

  public void setCurrentContext(Context currentContext) {
    this.currentContext = currentContext;
  }

  public void setCurrentPlayerIndex(int currentPlayerIndex) {
    this.currentPlayerIndex = currentPlayerIndex;
  }

  public void setNumberOfTradedSet(int numberOfTradedSet) {
    this.numberOfTradedSet = numberOfTradedSet;
  }

  public void setArmiesTradedForSet(int armiesTradedForSet) {
    this.armiesTradedForSet = armiesTradedForSet;
  }

  public void setCardCount(int cardCount) {
    this.cardCount = cardCount;
  }

  public static class GameStateBuilder {
    private ArrayList<PlayerState> playersList;
    private ArrayList<Card> deck;
    private Map<String, Set<String>> borders;
    private Map<String, Continent> continents;
    private Map<String, Country> countries;
    private Context currentContext;
    private int currentPlayerIndex;
    private int numberOfTradedSet;
    private int armiesTradedForSet;
    private int cardCount;

    private GameStateBuilder() {
    }

    public static GameStateBuilder aGameState() {
      return new GameStateBuilder();
    }

    public GameStateBuilder withPlayersList(ArrayList<PlayerState> playersList) {
      this.playersList = playersList;
      return this;
    }

    public GameStateBuilder withDeck(ArrayList<Card> deck) {
      this.deck = deck;
      return this;
    }

    public GameStateBuilder withBorders(Map<String, Set<String>> borders) {
      this.borders = borders;
      return this;
    }

    public GameStateBuilder withContinents(Map<String, Continent> continents) {
      this.continents = continents;
      return this;
    }

    public GameStateBuilder withCountries(Map<String, Country> countries) {
      this.countries = countries;
      return this;
    }

    public GameStateBuilder withCurrentContext(Context currentContext) {
      this.currentContext = currentContext;
      return this;
    }

    public GameStateBuilder withCurrentPlayerIndex(int currentPlayerIndex) {
      this.currentPlayerIndex = currentPlayerIndex;
      return this;
    }

    public GameStateBuilder withNumberOfTradedSet(int numberOfTradedSet) {
      this.numberOfTradedSet = numberOfTradedSet;
      return this;
    }

    public GameStateBuilder withArmiesTradedForSet(int armiesTradedForSet) {
      this.armiesTradedForSet = armiesTradedForSet;
      return this;
    }

    public GameStateBuilder withCardCount(int cardCount) {
      this.cardCount = cardCount;
      return this;
    }

    public GameState build() {
      GameState gameState = new GameState();
      gameState.setPlayersList(playersList);
      gameState.setDeck(deck);
      gameState.setBorders(borders);
      gameState.setContinents(continents);
      gameState.setCountries(countries);
      gameState.setCurrentContext(currentContext);
      gameState.setCurrentPlayerIndex(currentPlayerIndex);
      gameState.setNumberOfTradedSet(numberOfTradedSet);
      gameState.setArmiesTradedForSet(armiesTradedForSet);
      gameState.setCardCount(cardCount);
      return gameState;
    }
  }
}
