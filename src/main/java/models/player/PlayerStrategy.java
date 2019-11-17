package models.player;

import models.Card;
import models.GameMap;
import views.CardExchangeView;

import java.util.ArrayList;

/**
 * This interface holds the methods for implementing a Player Strategy
 */
public interface PlayerStrategy {

  /**
   * The attack command allows the user to attack another territory.
   * @param gameMap the GameMap instance
   * @param command the command entered by the user
   * @return boolean if the command was successful or not
   */
  boolean attack(GameMap gameMap, String command);

  /**
   * The reinforce command allows the user to reinforce their territories.
   * @param gameMap the GameMap instance
   * @param countryToPlace the country to reinforce
   * @param armiesToPlace the number of armies
   * @return boolean if the command was successful or not
   */
  boolean reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace);

  /**
   * The fortify command allows the user to fortify their territories.
   * @param gameMap the GameMap instance
   * @param fromCountry the country to move from
   * @param toCountry the country to move to
   * @param armyToMove the number of armies
   * @return boolean if the command was successful or not.
   */
  boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove);

  void addCard(Card card);
  int giveArmies();
  void subtractArmies(int count);
  void exchangeCardsForArmies(int[] indices);
  ArrayList<Card> getCardsInHand();
  void setCardsInHand(ArrayList<Card> cardsInHand);
  void setPlayerName(String playerName);
  String getPlayerName();
  int getNumberOfArmies();
  void setNumberOfArmies(int numberOfArmies);
  void addObserver(CardExchangeView instance);
}
