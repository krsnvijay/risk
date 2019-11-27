package models.player;

import controllers.BattleController;
import controllers.GameController;
import models.Card;
import models.Country;
import models.GameMap;
import views.CardExchangeView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;
import static views.ConsoleView.display;

/**
 * This class implements the Random player strategy.
 *
 * @author Siddharth
 * @version 1.0
 */
public class PlayerRandom extends Observable implements PlayerStrategy {
  /**
   * This instance variable holds the name of the player.
   */
  private String playerName = "Random";

  /**
   * Stores the number of armies a player has.
   */
  private int numberOfArmies;

  /**
   * Stores the cards currently held by the player.
   */
  private ArrayList<Card> cardsInHand = new ArrayList<>();

  /**
   * Generate random numbers for the player.
   */
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
    boolean continueAttack = randomGenerator.nextBoolean();
    if (!continueAttack) {
      return "attack -noattack";
    }

    ArrayList<Country> countries =
        Player.getCountriesByOwnership(playerName, gameMap).stream()
            .filter(c -> c.getNumberOfArmies() > 1)
            .collect(toCollection(ArrayList::new));
    if (countries.isEmpty()) {
      return "attack -noattack";
    }
    Country attackFromCountry = countries.get(randomGenerator.nextInt(countries.size()));
    ArrayList<String> attackToCountriesNames =
        new ArrayList<>(gameMap.getBorders().get(attackFromCountry.getName()));
    ArrayList<Country> attackToCountries =
        attackToCountriesNames.stream()
            .map(gameMap.getCountries()::get)
            .filter(c -> !c.getOwnerName().equals(playerName))
            .collect(toCollection(ArrayList::new));
    if (attackToCountries.isEmpty()) {
      return "attack -noattack";
    }
    Country attackToCountry =
        attackToCountries.get(randomGenerator.nextInt(attackToCountries.size()));
    String attackCommand;
    if (!attackToCountries.isEmpty() && attackToCountry != null) {
      attackCommand =
          String.format(
              "attack %s %s -allout", attackFromCountry.getName(), attackToCountry.getName());
    } else {
      attackCommand = "attack -noattack";
    }
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
    String attackCommand = randomAttack(gameMap);
    while (!attackCommand.contains("-noattack")) {
      if (GameController.validateAttack(gameMap, attackCommand)) {
        display(attackCommand, true);
        BattleController battleController = new BattleController(gameMap, attackCommand);
        battleController.setNoInputEnabled(true);
        battleController.startBattle();
        if (GameController.isTournament && GameMap.isGameOver) {
          return true;
        }
      } else {
        display("Invalid command", false);
      }
      attackCommand = randomAttack(gameMap);
    }
    GameController.processAttackCommand(gameMap,attackCommand);
    return true;
  }

  /**
   * the player will reinforce a random country
   *
   * @param gameMap        the GameMap instance
   * @param countryToPlace the country to reinforce
   * @param armiesToPlace  the number of armies
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
    if (cardsInHand.size() >= 5) {
      this.exchangeCardsForArmies(Player.getCardExchangeIndices(getCardsInHand()));
    }
    return true;
  }

  /**
   * the player will fortify a random country
   *
   * @param gameMap     the GameMap instance
   * @param fromCountry the country to move from
   * @param toCountry   the country to move to
   * @param armyToMove  the number of armies
   * @return check whether fortify is possible
   */
  @Override
  public boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove) {
    String fortifyCommand = randomFortify(gameMap);
    if (fortifyCommand.contains("-none")){
      GameController.processFortifyCommand(gameMap,fortifyCommand);
      return true;
    }
    if (GameController.validateFortify(gameMap, fortifyCommand))
      return GameController.processFortifyCommand(gameMap, fortifyCommand);
    return false;
  }

  /**
   * Handles the fortify command, randomly.
   *
   * @param gameMap The GameMap object.
   * @return The command to execute for the fortify phase.
   */
  public String randomFortify(GameMap gameMap) {
    boolean shouldFortify = GameMap.getRandomGenerator().nextBoolean();
    if (!shouldFortify)
      return "fortify -none";
    ArrayList<Country> countries = Player.getCountriesByOwnership(playerName, gameMap).stream()
        .filter(c -> c.getNumberOfArmies() > 1)
        .filter(c -> gameMap.getBorders().get(c.getName()).stream()
            .map(gameMap.getCountries()::get).anyMatch(neighbor -> neighbor.getOwnerName().equals(c.getOwnerName())))
        .collect(toCollection(ArrayList::new));
    if (countries.isEmpty())
      return "fortify -none";
    Country fortifyFromCountry = countries.get(randomGenerator.nextInt(countries.size()));
    ArrayList<Country> neighboringCountry =
        gameMap.getBorders().get(fortifyFromCountry.getName()).stream()
            .map(gameMap.getCountries()::get)
            .filter(c -> c.getOwnerName().equals(fortifyFromCountry.getOwnerName()))
            .collect(Collectors.toCollection(ArrayList::new));
    if (neighboringCountry.isEmpty())
      return "fortify -none";
    Country fortifyToCountry = neighboringCountry.get(randomGenerator.nextInt(neighboringCountry.size()));
    int fortifyArmies = randomGenerator.nextInt(fortifyFromCountry.getNumberOfArmies() - 1);
    if (fortifyArmies < 1)
      fortifyArmies = 1;
    return String.format("fortify %s %s %d", fortifyFromCountry.getName(), fortifyToCountry.getName(), fortifyArmies);

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

  /**
   * Returns the strategy type
   * @return A String with the strategy's name.
   */
  @Override
  public String getStrategyType() {
    return "random";
  }

  /**
   * This is an override for pretty printing the name.
   */
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
}
