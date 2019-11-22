package models.player;

import controllers.BattleController;
import models.Card;
import models.Country;
import models.GameMap;
import views.CardExchangeView;

import java.util.*;
import java.util.stream.Collectors;

import static views.ConsoleView.display;

public class PlayerRandom extends Observable implements PlayerStrategy {
  /** Maintains the number of sets traded in game */
  private static int numberOfTradedSet = 0;
  /** Number of armies traded in for each set */
  private static int armiesTradedForSet = 0;
  /** This instance variable holds the name of the player. */
  private String playerName = "Random";
  /** Stores the number of armies a player has. */
  private int numberOfArmies;
  /** Stores the cards currently held by the player. */
  private ArrayList<Card> cardsInHand = new ArrayList<>();
  /** How many turns have elapsed */
  private int turnCount = 0;
  /** Generate random numbers for the player. */
  private Random randomGenerator = GameMap.getRandomGenerator();

  /**
   * Constructor for Random Player object
   *
   * @param name sets the name of the player
   */
  public PlayerRandom(String name) {
    this.setPlayerName(name);
  }


  /**
   * Update the CardExchangeView with the context
   *
   * @param object to set the context
   */
  public void addObserver(CardExchangeView object) {
    super.addObserver(object);
  }

  /**
   * Method to select a random country to attack
   *
   * @param gameMap Instance object of the game
   * @return the command by the random player
   */
  public String randomAttack(GameMap gameMap) {
    ArrayList<Country> countries = Player.getCountriesByOwnership(playerName, gameMap);
    Country attackFromCountry = countries.get(randomGenerator.nextInt(countries.size()));
    ArrayList<String> attackToCountries =
        new ArrayList<>(gameMap.getBorders().get(attackFromCountry.getName()));
    String attackToCountry =
        attackToCountries.get(randomGenerator.nextInt(attackToCountries.size()));
    boolean continueAttack = randomGenerator.nextBoolean();
    String attackCommand = null;
    if (continueAttack)
      attackCommand =
          String.format("attack %s %s -allout", attackFromCountry.getName(), attackToCountry);
    else attackCommand = "attack -noattack";
    return attackCommand;
  }

  /**
   * The player will attack a random country for a random number of times
   *
   * @param gameMap the GameMap instance
   * @param command the command entered by the user
   * @return if the attack is possible or not
   */
  @Override
  public boolean attack(GameMap gameMap, String command) {
    String attackCommand = null;
    do {
      attackCommand = randomAttack(gameMap);
      BattleController battleController = new BattleController(gameMap, attackCommand);
      battleController.startBattle();
    } while (!attackCommand.contains("-noattack"));
    return true;
  }

  /**
   * the player will reinforce a random country
   *
   * @param gameMap the GameMap instance
   * @param countryToPlace the country to reinforce
   * @param armiesToPlace the number of armies
   * @return check whether reinforce is possible
   */
  @Override
  public boolean reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace) {
    ArrayList<Country> countries = Player.getCountriesByOwnership(playerName, gameMap);
    Country reinforcedCountry = countries.get(randomGenerator.nextInt(countries.size()));
    reinforcedCountry.addArmies(armiesToPlace);
    display(
        String.format(
            "%s has placed %d army(s) in %s",
            playerName, armiesToPlace, reinforcedCountry.getName()),
        true);
    if (cardsInHand.size() > 3) {
      this.exchangeCardsForArmies(Player.getCardExchangeIndices(this.getCardsInHand()));
    }
    return true;
  }

  /**
   * the player will fortify a random country
   *
   * @param gameMap the GameMap instance
   * @param fromCountry the country to move from
   * @param toCountry the country to move to
   * @param armyToMove the number of armies
   * @return check whether fortify is possible
   */
  @Override
  public boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove) {
    ArrayList<Country> countries = Player.getCountriesByOwnership(playerName, gameMap);
    Collections.shuffle(countries);
    Country fortifyFromCountry = countries.get(0);
    ArrayList<String> neighboringCountry =
        new ArrayList<>(gameMap.getBorders().get(fortifyFromCountry.getName()));
    Country fortifyToCountry = countries.get(randomGenerator.nextInt(neighboringCountry.size()));
    if (fortifyFromCountry.getNumberOfArmies() > 2) {
      int fortifyArmies = randomGenerator.nextInt(fortifyFromCountry.getNumberOfArmies() - 2);
      fortifyToCountry.removeArmies(fortifyArmies);
      fortifyToCountry.addArmies(fortifyArmies);
      display(
          String.format(
              "%s Fortified %s with %d army(s) from %s",
              playerName, fortifyToCountry.getName(), fortifyArmies, fortifyFromCountry.getName()),
          true);
    } else {
      display(String.format("%s chose not to fortify", playerName), true);
    }
    turnCount++;
    return true;
  }

  /**
   * This method gives armies to the player
   *
   * @return int with the number of armies.
   */
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
   * This method removes armies from the player
   *
   * @param count armies to subtract from the player
   */
  public void subtractArmies(int count) {
    this.numberOfArmies -= count;
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

      display("Acquired " + armiesAcquired + " through card exchange", false);
    } else {
      display(
          "The set provided is not valid. Valid set: 3 cards of same type or 3 cards of different type",
          false);
    }
  }
}
