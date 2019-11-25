package models.player;

import controllers.BattleController;
import models.Card;
import models.GameMap;
import views.CardExchangeView;

import java.util.*;
import java.util.stream.Collectors;

import static views.ConsoleView.display;

/**
 * This is the Player class which handles every player.
 *
 * @author Sabari
 * @version 1.0
 */
public class PlayerHuman extends Observable implements PlayerStrategy {


  /** This instance variable holds the name of the player. */
  private String playerName;
  /** Stores the number of armies a player has. */
  private int numberOfArmies;
  /** Stores the cards currently held by the player. */
  private ArrayList<Card> cardsInHand = new ArrayList<>();
  /** How many turns have elapsed */
  private int turnCount = 0;

  /**
   * This constructor initializes the class.
   *
   * @param playerName name of the player
   */
  public PlayerHuman(String playerName) {
    super();
    this.playerName = playerName;
  }

  public void addObserver(CardExchangeView object) {
    super.addObserver(object);
  }

  /**
   * This method returns the name of the player.
   *
   * @return playerName the name of the player.
   */
  public String getPlayerName() {
    return playerName;
  }

  /**
   * This method sets the name of the player.
   *
   * @param playerName the name of the player.
   */
  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  /**
   * Getter for number of armies the player owns.
   *
   * @return int with number of armies
   */
  public int getNumberOfArmies() {
    return this.numberOfArmies;
  }

  /**
   * Setter for number of armies the player owns.
   *
   * @param numberOfArmies int with the number of armies.
   */
  public void setNumberOfArmies(int numberOfArmies) {
    this.numberOfArmies = numberOfArmies;
  }

  @Override
  public String getStrategyType() {
    return "human";
  }

  /** This is an override for pretty printing the name. */
  @Override
  public String toString() {
    return String.format("%s", this.playerName);
  }

  /**
   * Returns the player's hand.
   *
   * @return List with the Cards
   */
  public ArrayList<Card> getCardsInHand() {
    return cardsInHand;
  }

  /**
   * Sets the cards in the player's hand.
   *
   * @param cardsInHand A collection of Card objects.
   */
  public void setCardsInHand(ArrayList<Card> cardsInHand) {
    this.cardsInHand = cardsInHand;
    setChanged();
    notifyObservers();
  }

  /**
   * Adds a card to this player's hand.
   *
   * @param card The Card object to be added.
   */
  public void addCard(Card card) {
    this.cardsInHand.add(card);
    setChanged();
    notifyObservers();
  }


  /**
   * Reinforce a currently owned country with an army
   *
   * @param countryToPlace name of country
   * @param armiesToPlace count of armies to place
   * @param gameMap the Game Map instance
   * @return boolean to indicate success or failure
   */
  public boolean reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace) {
    return gameMap.placeArmy(countryToPlace, armiesToPlace);
  }

  /**
   * Moves armies from one adjacent country to the other
   *
   * @param gameMap the Game Map instance
   * @param fromCountry country name to move from
   * @param toCountry contry name to move to
   * @param armyToMove no of armies to move
   * @return boolean to indicate status
   */
  public boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove) {
    boolean result = false;
    boolean isArmyRemoved = gameMap.getCountries().get(fromCountry).removeArmies(armyToMove);
    if (isArmyRemoved) {
      gameMap.getCountries().get(toCountry).addArmies(armyToMove);
      result = true;
    }
    if (result) this.turnCount++;
    return result;
  }



  /**
   * This method removes armies from the player
   *
   * @param count armies to subtract from the player
   */
  public void subtractArmies(int count) {
    this.numberOfArmies -= count;
  }

  /** Check whether one Player object is equal to another. */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PlayerHuman player = (PlayerHuman) o;
    return numberOfArmies == player.numberOfArmies && playerName.equals(player.playerName);
  }

  /** @return int hash code of the Player object. */
  @Override
  public int hashCode() {
    return Objects.hash(playerName, numberOfArmies);
  }

  /**
   * Starts the battle loop.
   *
   * @param gameMap The Game Map instance.
   * @param command The command string.
   * @return a boolean with the result.
   */
  public boolean attack(GameMap gameMap, String command) {
    // Start battle loop
    BattleController battleController = new BattleController(gameMap, command);
    return battleController.startBattle();
  }
}
