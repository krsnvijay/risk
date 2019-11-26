package models.player;

import controllers.BattleController;
import controllers.GameController;
import models.Card;
import models.Context;
import models.Country;
import models.GameMap;
import views.CardExchangeView;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static views.ConsoleView.display;

public class PlayerAggressive extends Observable implements PlayerStrategy {
  /** This instance variable holds the name of the player. */
  private String playerName;
  /** Stores the number of armies a player has. */
  private int numberOfArmies;
  /** Stores the cards currently held by the player. */
  private ArrayList<Card> cardsInHand = new ArrayList<>();

  public PlayerAggressive(String name) {
    this.setPlayerName(name);
  }

  public void addObserver(CardExchangeView object) {
    super.addObserver(object);
  }

  private Map.Entry<String, Country> getStrongestCountryAttackReinforce(
      GameMap gameMap, String op) {
    ArrayList<Country> countriesOwnedByPlayer =
        Player.getCountriesByOwnership(this.getPlayerName(), gameMap);

    if (op.equals("attack")) {
      countriesOwnedByPlayer =
          countriesOwnedByPlayer.stream()
              .filter(c -> c.getNumberOfArmies() > 1)
              .sorted(Comparator.comparingInt(Country::getNumberOfArmies).reversed())
              .collect(toCollection(ArrayList::new));
    } else {
      countriesOwnedByPlayer =
          countriesOwnedByPlayer.stream()
              .sorted(Comparator.comparingInt(Country::getNumberOfArmies).reversed())
              .collect(toCollection(ArrayList::new));
    }

    Optional<Country> resEntry =
        countriesOwnedByPlayer.stream()
            .filter(
                country ->
                    !gameMap.getBorders().get(country.getName()).stream()
                        .map(gameMap.getCountries()::get)
                        .allMatch(neighbor -> neighbor.getOwnerName().equals(this.playerName)))
            .findFirst();

    if (resEntry.isPresent())
      return new AbstractMap.SimpleEntry<>(resEntry.get().getName(), resEntry.get());
    else {
      // SHOULD NEVER HAPPEN
      System.out.println(countriesOwnedByPlayer.stream().map(Country::getName).collect(joining(" ")));
      //countriesOwnedByPlayer.forEach(c -> { System.out.println(gameMap.getBorders().get(c.getName()) + " " + gameMap.getBorders().get(c.getOwnerName())); });
      return null;
    }
  }

  private Map.Entry<String, Country> getStrongestCountryFortify(GameMap gameMap) {
    ArrayList<Country> countriesOwnedByPlayer =
        Player.getCountriesByOwnership(this.getPlayerName(), gameMap);
    countriesOwnedByPlayer =
        countriesOwnedByPlayer.stream()
            .sorted(Comparator.comparingInt(Country::getNumberOfArmies).reversed())
            .collect(toCollection(ArrayList::new));
    Map.Entry<String, Country> resEntry = null;
    for (Country c : countriesOwnedByPlayer) {
      boolean noOwnedNeighbor =
          gameMap.getBorders().get(c.getName()).stream()
              .noneMatch(
                  neighbor ->
                      gameMap.getCountries().get(neighbor).getOwnerName().equals(this.playerName));
      if (!noOwnedNeighbor) {
        resEntry = new AbstractMap.SimpleEntry<String, Country>(c.getName(), c);
        break;
      }
    }
    return resEntry;
  }

  private void attackUtil(GameMap gameMap, Map.Entry<String, Country> countryWithMaxArmies) {
    if (countryWithMaxArmies == null) {
      display("No strongest country left to attack", true);
      GameController.assignedCard = false;
        gameMap.setCurrentContext(Context.GAME_FORTIFY);
      return;
    }
    String countryToAttack =
        gameMap.getBorders().get(countryWithMaxArmies.getKey()).stream()
            .filter(
                neighbor ->
                    !gameMap.getCountries().get(neighbor).getOwnerName().equals(this.playerName))
            .reduce(
                null,
                (minArmiesNeighbor, country) -> {
                  if (minArmiesNeighbor == null) return country;
                  Country minArmiesNeighborCountry = gameMap.getCountries().get(minArmiesNeighbor);
                  Country currentCountry = gameMap.getCountries().get(country);
                  if (currentCountry.getNumberOfArmies()
                      < minArmiesNeighborCountry.getNumberOfArmies()) return country;
                  return minArmiesNeighbor;
                });

    String command =
        String.format("attack %s %s -allout", countryWithMaxArmies.getKey(), countryToAttack);
    if (GameController.validateAttack(gameMap, command)) {
      BattleController battleController = new BattleController(gameMap, command);
      battleController.setNoInputEnabled(true);
      battleController.startBattle();
    }
  }

  @Override
  public boolean attack(GameMap gameMap, String blankCommand) {
    // recursively with strongest valid country until he can't attack
    while (gameMap.getCurrentContext().name().contains("ATTACK")) {
      if (GameController.isTournament && GameMap.isGameOver) {
        return true;
      }
      Map.Entry<String, Country> countryWithMaxArmies =
          getStrongestCountryAttackReinforce(gameMap, "attack");
      attackUtil(gameMap, countryWithMaxArmies);
    }
    return true;
  }

  @Override
  public boolean reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace) {
    // move all armies to strongest country
    // check if cardExchange is possible
    // perform cardExchange and then reinforce
    PlayerAggressive currPlayer = (PlayerAggressive) gameMap.getCurrentPlayer().getStrategy();
    if (cardsInHand.size() >= 5) {
      currPlayer.exchangeCardsForArmies(Player.getCardExchangeIndices(this.getCardsInHand()));
    }
    int armiesToReinforce = currPlayer.getNumberOfArmies();
    String countryToReinforce = getStrongestCountryAttackReinforce(gameMap, "reinforce").getKey();
    gameMap.placeArmy(countryToReinforce, armiesToReinforce);
    display(
        String.format(
            "%s reinforced %s with %d armies", currPlayer, countryToReinforce, armiesToReinforce),
        true);
    return true;
  }

  @Override
  public boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove) {
    // get the country with max number of armies
    // check if fortify possible
    // aggregate armies in this country FROM the next strongest country
    boolean result = false;

    String currPlayerName = gameMap.getCurrentPlayer().getStrategy().getPlayerName();
    Map.Entry<String, Country> tCountryEntry = getStrongestCountryFortify(gameMap);
    Country tCountry = tCountryEntry == null ? null : tCountryEntry.getValue();
    Country fCountry;
    String fCountryStr = null;
    String tCountryStr = null;
    if (tCountry != null) {
      fCountry = findStrongestAlongDFSPath(gameMap, currPlayerName, tCountry.getName());
      if (fCountry == null) {
        display(String.format("%s chose not to fortify", currPlayerName), true);
        return true;
      }
      fCountryStr = fCountry.getName();
      tCountryStr = tCountry.getName();
    }

    if (fCountryStr != null && tCountryStr != null) {
      int fCountryArmies = gameMap.getCountries().get(fCountryStr).getNumberOfArmies();
      int armiesToMove = 0;

      if (fCountryArmies < 2) {
        display(String.format("%s chose not to fortify", currPlayerName), true);
        return true;
      } else {
        armiesToMove = gameMap.getCountries().get(fCountryStr).getNumberOfArmies() - 1;
        boolean isArmyRemoved = gameMap.getCountries().get(fCountryStr).removeArmies(armiesToMove);
        if (isArmyRemoved) {
          gameMap.getCountries().get(tCountryStr).addArmies(armiesToMove);
          result = true;
        }
      }

      if (result) {
        display(
            String.format(
                "%s Fortified %s with %d army(s) from %s",
                currPlayerName, tCountryStr, armiesToMove, fCountryStr),
            true);
      }
    } else {
      display(String.format("%s chose not to fortify", currPlayerName), true);
      result = true;
    }
    return result;
  }

  private Country findStrongestAlongDFSPath(
      GameMap gameMap, String currPlayerName, String maxCountry) {
    ArrayList<Country> countriesOwnedByPlayer =
        Player.getCountriesByOwnership(currPlayerName, gameMap);
    ArrayList<String> countriesOwnedByPlayerStrings =
        countriesOwnedByPlayer.stream().map(Country::getName).collect(toCollection(ArrayList::new));

    Map<String, Set<String>> copyMapBorders = new HashMap<>(gameMap.getBorders());
    Map<String, Set<String>> filteredGameMap =
        copyMapBorders.entrySet().stream()
            .filter(entry -> countriesOwnedByPlayerStrings.contains(entry.getKey()))
            .map(
                entry ->
                    new AbstractMap.SimpleEntry<>(
                        entry.getKey(),
                        entry.getValue().stream()
                            .filter(countriesOwnedByPlayerStrings::contains)
                            .collect(toSet())))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

    ArrayList<String> DFSNeighbors =
        new ArrayList<>(DFSUtil(gameMap, filteredGameMap, new HashSet<>(), maxCountry));
    DFSNeighbors.remove(maxCountry);
    String fromFortifyCountry =
        DFSNeighbors.isEmpty() ? "none" : getFromFortifyCountry(gameMap, DFSNeighbors);

    if (!fromFortifyCountry.equals("none")) return gameMap.getCountries().get(fromFortifyCountry);
    return null;
  }

  private Set<String> DFSUtil(
      GameMap gameMap,
      Map<String, Set<String>> filteredGameMap,
      Set<String> visited,
      String start) {
    visited.add(start);
    for (String neighbor : filteredGameMap.get(start)) {
      if (!visited.contains(neighbor)) {
        DFSUtil(gameMap, filteredGameMap, visited, neighbor);
      }
    }
    return visited;
  }

  private String getFromFortifyCountry(GameMap gameMap, ArrayList<String> DFSNeighbors) {
    ArrayList<Country> sortedReverse =
        DFSNeighbors.stream()
            .map(neighbor -> gameMap.getCountries().get(neighbor))
            .sorted(Comparator.comparingInt(Country::getNumberOfArmies).reversed())
            .collect(Collectors.toCollection(ArrayList::new));
    return sortedReverse.get(0).getName();
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
    return "aggressive";
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

}
