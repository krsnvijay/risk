package models.player;

import models.Card;
import models.GameMap;
import views.CardExchangeView;

import java.util.*;
import java.util.stream.Collectors;

import static views.ConsoleView.display;

/** This interface holds the methods for implementing a Player Strategy */
public interface PlayerStrategy {

  /**
   * The attack command allows the user to attack another territory.
   *
   * @param gameMap the GameMap instance
   * @param command the command entered by the user
   * @return boolean if the command was successful or not
   */
  boolean attack(GameMap gameMap, String command);

  /**
   * The reinforce command allows the user to reinforce their territories.
   *
   * @param gameMap the GameMap instance
   * @param countryToPlace the country to reinforce
   * @param armiesToPlace the number of armies
   * @return boolean if the command was successful or not
   */
  boolean reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace);

  /**
   * The fortify command allows the user to fortify their territories.
   *
   * @param gameMap the GameMap instance
   * @param fromCountry the country to move from
   * @param toCountry the country to move to
   * @param armyToMove the number of armies
   * @return boolean if the command was successful or not.
   */
  boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove);

  void addCard(Card card);

  void subtractArmies(int count);

  ArrayList<Card> getCardsInHand();

  void setCardsInHand(ArrayList<Card> cardsInHand);

  String getPlayerName();

  void setPlayerName(String playerName);

  int getNumberOfArmies();

  void setNumberOfArmies(int numberOfArmies);

  String getStrategyType();

  void addObserver(CardExchangeView instance);

  public
  /**
   * Exchange the card for armies.
   *
   * @param indices the positions of the cards in the list.
   */
  default void exchangeCardsForArmies(int[] indices) {
    ArrayList<Card> cardsInHand = getCardsInHand();
    Set<String> cardSet = new HashSet<>();
    for (int index : indices) {
      if (index >= 0 && index < cardsInHand.size()) {
        cardSet.add(cardsInHand.get(index).getCardValue());
      } else {
        display("One OR more of your card indices are INCORRECT", false);
        return;
      }
    }
    if (cardSet.size() == 1 || cardSet.size() == 3) {
      GameMap.setNumberOfTradedSet(GameMap.getNumberOfTradedSet() + 1);
      int armiesAcquired = giveArmies(GameMap.getNumberOfTradedSet(),GameMap.getArmiesTradedForSet());
      setNumberOfArmies(getNumberOfArmies() + armiesAcquired);

      ArrayList<Card> cardsToAddToDeck = new ArrayList<>();
      for (int index : indices) {
        cardsToAddToDeck.add(cardsInHand.get(index));
      }

      ArrayList<Integer> listIndices =
          Arrays.stream(indices)
              .boxed()
              .sorted(Comparator.reverseOrder())
              .collect(Collectors.toCollection(ArrayList::new));

      ArrayList<Card> resultCardsInHand = new ArrayList<>();
      for (int i = 0; i < cardsInHand.size(); i++) {
        if (!listIndices.contains(i)) {
          resultCardsInHand.add(cardsInHand.get(i));
        }
      }
      setCardsInHand(resultCardsInHand);

      Collections.shuffle(cardsToAddToDeck);
      GameMap.getGameMap()
          .getDeck()
          .addAll(
              cardsToAddToDeck); // add the exchanged cards to deck after removing from player hand
      display(String.format("%s exchanges 3 cards for armies.", getPlayerName()), true);
      display("Acquired " + armiesAcquired + " through card exchange", false);
    } else {
      display(
          "The set provided is not valid. Valid set: 3 cards of same type or 3 cards of different type",
          false);
    }
  }

  /**
   * This method gives armies to the player
   *
   * @return int with the number of armies.
   */
  public default int giveArmies(int numberOfTradedSet,int armiesTradedForSet) {
    if (numberOfTradedSet == 1) {
      armiesTradedForSet += 4;
    } else if (numberOfTradedSet < 6) {
      armiesTradedForSet += 2;
    } else if (numberOfTradedSet == 6) {
      armiesTradedForSet += 3;
    } else {
      armiesTradedForSet += 5;
    }

    return armiesTradedForSet;
  }
}
