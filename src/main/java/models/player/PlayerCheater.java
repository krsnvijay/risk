package models.player;

import models.Card;
import models.Country;
import models.GameMap;
import views.CardExchangeView;
import java.util.*;
import java.util.stream.Collectors;
import static views.ConsoleView.display;

/**
 * This is the Strategy for the Cheater player.
 *
 * @author Siddhant Bansal
 * @version 1.0
 */
public class PlayerCheater extends Observable implements PlayerStrategy {
  /** Maintains the number of sets traded in game */
  private static int numberOfTradedSet = 0;
  /** Number of armies traded in for each set */
  private static int armiesTradedForSet = 0;
  /** This instance variable holds the name of the player. */
  private String playerName;
  /** Stores the number of armies a player has. */
  private int numberOfArmies;
  /** Stores the cards currently held by the player. */
  private ArrayList<Card> cardsInHand = new ArrayList<>();
  /** How many turns have elapsed */
  private int turnCount = 0;

  /**
   * The constructor for the Cheater strategy class.
   * @param name the name of the player.
   */
  public PlayerCheater(String name) {
    this.setPlayerName(name);
  }

  /**
   * Registers this class as an observer
   * @param object the CardExchangeView to register with.
   */
  public void addObserver(CardExchangeView object) {
    super.addObserver(object);
  }

  /**
   * The Cheater's attack will capture all neighbors of owned countries.
   *
   * @param gameMap the GameMap instance
   * @param command the command entered by the user
   * @return the boolean result of the command; always true for AI.
   */
  @Override
  public boolean attack(GameMap gameMap, String command) {
    display(String.format("%s attacks and captures all opposing neighbours.", playerName), true);
    ArrayList<Country> countries = Player.getCountriesByOwnership(playerName, gameMap).stream()
        .filter(c -> c.getNumberOfArmies() > 2).collect(Collectors.toCollection(ArrayList::new));
    Map<String, Country> allCountries = gameMap.getCountries();
    for(Country cheaterCountry: countries) {
      Set<String> borders = gameMap.getBorders().get(cheaterCountry.getName());
      for(String neighborName : borders) {
        Country neighbor = allCountries.get(neighborName);
        if(!neighbor.getOwnerName().equals(playerName)) {
          neighbor.setOwnerName(playerName);
          neighbor.setNumberOfArmies(cheaterCountry.getNumberOfArmies()/2);
          cheaterCountry.removeArmies(cheaterCountry.getNumberOfArmies()/2);
        }
        if(cheaterCountry.getNumberOfArmies() == 1) break;
      }
    }
    return true;
  }

  /**
   * The Cheater's reinforce command doubles armies on all countries owned.
   *
   * @param gameMap the GameMap instance
   * @param countryToPlace the country to reinforce
   * @param armiesToPlace the number of armies
   * @return the boolean result of the command; always true for AI.
   */
  @Override
  public boolean reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace) {
    display(String.format("%s Reinforces, all owned countries have double the armies now.", playerName), true);
    ArrayList<Country> countries = Player.getCountriesByOwnership(playerName, gameMap);
    gameMap.getCurrentPlayer().getStrategy().setNumberOfArmies(0);
    countries.forEach(country -> country.addArmies(country.getNumberOfArmies()));
    if(cardsInHand.size() > 3) {
      this.exchangeCardsForArmies(Player.getCardExchangeIndices(this.getCardsInHand()));
    }
    return true;
  }

  /**
   * The Cheater's fortify will double armies on all countries with neighbors belonging to other players.
   *
   * @param gameMap the GameMap instance
   * @param fromCountry the country to move from
   * @param toCountry the country to move to
   * @param armyToMove the number of armies
   * @return the boolean result of the command; always true for AI.
   */
  @Override
  public boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove) {
    display(String.format("%s Fortifies, all countries with opposing neighbors " +
        "have double the armies now.", playerName), true);
    ArrayList<Country> countries = Player.getCountriesByOwnership(playerName, gameMap);
    Map<String, Country> allCountries = gameMap.getCountries();
    for(Country cheaterCountry: countries) {
      Set<String> borders = gameMap.getBorders().get(cheaterCountry.getName());
      for(String neighborName : borders) {
        Country neighbor = allCountries.get(neighborName);
        if(!neighbor.getOwnerName().equals(playerName)) {
          cheaterCountry.addArmies(cheaterCountry.getNumberOfArmies());
        }
      }
    }
    turnCount++;
    return true;
  }

  /** This method gives armies to the player
   * @return int with the number of armies.
   * */
  public int giveArmies() {
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
  public void exchangeCardsForArmies(int[] indices) {
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
      numberOfTradedSet++;
      int armiesAcquired = giveArmies();
      numberOfArmies += armiesAcquired;

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
      for(int i = 0;i<cardsInHand.size();i++){
        if(!listIndices.contains(i)){
          resultCardsInHand.add(cardsInHand.get(i));
        }
      }
      setCardsInHand(resultCardsInHand);

      Collections.shuffle(cardsToAddToDeck);
      GameMap.getGameMap()
          .getDeck()
          .addAll(
              cardsToAddToDeck); // add the exchanged cards to deck after removing from player hand
      display(String.format("%s exchanges 3 cards for armies.", playerName), true);
      display("Acquired " + armiesAcquired + " through card exchange", false);
    } else {
      display(
          "The set provided is not valid. Valid set: 3 cards of same type or 3 cards of different type",
          false);
    }
  }
}
