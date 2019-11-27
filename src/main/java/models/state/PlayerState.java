package models.state;

import models.Card;

import java.util.ArrayList;

/**
 * This class maintains the state of one player.
 *
 * @author Vijay
 * @version 1.0
 */
public class PlayerState {
  /** Stores the strategy of the player */
  private String strategy;

  /** This instance variable holds the name of the player. */
  private String playerName;

  public PlayerState(String strategy, String playerName, int numberOfArmies, ArrayList<Card> cardsInHand) {
    this.strategy = strategy;
    this.playerName = playerName;
    this.numberOfArmies = numberOfArmies;
    this.cardsInHand = cardsInHand;
  }

  /** Stores the number of armies a player has. */
  private int numberOfArmies;

  /** Stores the cards currently held by the player. */
  private ArrayList<Card> cardsInHand = new ArrayList<>();

  /**
   * Gets the strategy for the player.
   *
   * @return The strategy as a String.
   */
  public String getStrategy() {
    return strategy;
  }

  /**
   * Gets the player name.
   *
   * @return The name as a string.
   */
  public String getPlayerName() {
    return playerName;
  }

  /**
   * Gets the number of armies.
   *
   * @return An integer with the number of armies.
   */
  public int getNumberOfArmies() {
    return numberOfArmies;
  }

  /**
   * Gets the cards in the Player's hand.
   *
   * @return An ArrayList of Card objects.
   */
  public ArrayList<Card> getCardsInHand() {
    return cardsInHand;
  }

  /**
   * Sets the strategy for the player.
   *
   * @param strategy A String object naming the strategy.
   */
  public void setStrategy(String strategy) {
    this.strategy = strategy;
  }

  /**
   * Sets the name of the player.
   *
   * @param playerName A string with the name.
   */
  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  /**
   * Sets the number of armies for the player.
   *
   * @param numberOfArmies An integer.
   */
  public void setNumberOfArmies(int numberOfArmies) {
    this.numberOfArmies = numberOfArmies;
  }

  /**
   * Sets the cards in hand.
   *
   * @param cardsInHand An ArrayList of Cards.
   */
  public void setCardsInHand(ArrayList<Card> cardsInHand) {
    this.cardsInHand = cardsInHand;
  }
}
