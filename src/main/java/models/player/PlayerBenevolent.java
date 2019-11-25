package models.player;

import controllers.GameController;
import models.Card;
import models.Country;
import models.GameMap;
import views.CardExchangeView;

import java.util.*;

import static views.ConsoleView.display;

/**
 * This is the Strategy for the Benevolent player.
 *
 * @author Siddhant Bansal
 * @version 1.0
 */
public class PlayerBenevolent extends Observable implements PlayerStrategy {
  /** This instance variable holds the name of the player. */
  private String playerName;
  /** Stores the number of armies a player has. */
  private int numberOfArmies;
  /** Stores the cards currently held by the player. */
  private ArrayList<Card> cardsInHand = new ArrayList<>();

  /**
   * The constructor for the Benevolent strategy class.
   *
   * @param name
   */
  public PlayerBenevolent(String name) {
    this.setPlayerName(name);
  }

  /**
   * Registers this class as an observer
   *
   * @param object the CardExchangeView to register with.
   */
  public void addObserver(CardExchangeView object) {
    super.addObserver(object);
  }

  /**
   * The Benevolent player never attacks.
   *
   * @param gameMap the GameMap instance
   * @param command the command entered by the user
   * @return the boolean result of the command; always true for AI.
   */
  @Override
  public boolean attack(GameMap gameMap, String command) {
    GameController.performAttackNone(gameMap);
    return true;
  }

  /**
   * The Benevolent player always reinforces the weakest country.
   *
   * @param gameMap the GameMap instance
   * @param countryToPlace the country to reinforce
   * @param armiesToPlace the number of armies
   * @return the boolean result of the command; always true for AI.
   */
  @Override
  public boolean reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace) {
    ArrayList<Country> countries = Player.getCountriesByOwnership(playerName, gameMap);
    Optional<Country> weakestCountry =
        countries.stream().min(Comparator.comparing(Country::getNumberOfArmies));
    weakestCountry.ifPresent(
        c -> {
          gameMap.placeArmy(c.getName(), armiesToPlace);
          display(String.format("%s reinforced by %s using %d armies.",
              c.getName(), playerName, armiesToPlace), true);
        });
    if (cardsInHand.size() > 3) {
      this.exchangeCardsForArmies(Player.getCardExchangeIndices(this.getCardsInHand()));
    }
    return true;
  }

  /**
   * The Benevolent player's fortify method will bolster the weakest country with one of its
   * strongest neighbors.
   *
   * @param gameMap the GameMap instance
   * @param fromCountry the country to move from
   * @param toCountry the country to move to
   * @param armyToMove the number of armies
   * @return the boolean result of the command; always true for AI.
   */
  @Override
  public boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove) {
    ArrayList<Country> countries = Player.getCountriesByOwnership(playerName, gameMap);
    Map<String, Country> allCountries = gameMap.getCountries();
    Optional<Country> weakestCountry =
        countries.stream().min(Comparator.comparing(Country::getNumberOfArmies));
    weakestCountry.ifPresent(
        weakest -> {
          Set<String> neighbors = gameMap.getBorders().get(weakest.getName());

          Optional<Country> strongestNeighbor =
              neighbors.stream()
                  .map(allCountries::get)
                  .max(Comparator.comparingInt(Country::getNumberOfArmies));

          if (strongestNeighbor.isPresent()) {
            Country target = strongestNeighbor.get();
            int halfTheArmies = (target.getNumberOfArmies() / 2);

            if (halfTheArmies == 0) {
              display(String.format("%s chose not to fortify", playerName), true);
              return;
            }

            boolean isArmyRemoved = target.removeArmies(halfTheArmies);
            if (isArmyRemoved) {
              weakest.addArmies(halfTheArmies);
              display(String.format("%s fortified by %s using %d armies.",
                  weakest.getName(), playerName, halfTheArmies), true);
            }
          }
        });
    return true;
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
   * @param playername the name of the player.
   */
  public void setPlayerName(String playername) {
    this.playerName = playername;
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
    return "benevolent";
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
   * This method removes armies from the player
   *
   * @param count armies to subtract from the player
   */
  public void subtractArmies(int count) {
    this.numberOfArmies -= count;
  }

  /**
   * Exchange the card for armies.
   *
   * @param indices the positions of the cards in the list.
   */
}
