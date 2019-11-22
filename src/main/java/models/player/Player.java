package models.player;

import models.Card;
import models.Country;
import models.GameMap;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

/**
 * This is the parent class for all the other player types.
 *
 * @author Siddhant Bansal
 * @author Warren White
 * @version 1.0
 */
public class Player {
  /** The strategy for the Player. */
  private PlayerStrategy strategy = null;

  public Player(String name, String strategy) {
    switch (strategy) {
      case "random":
        this.setStrategy(new PlayerRandom(name));
        break;
      case "aggressive":
        this.setStrategy(new PlayerAggressive(name));
        break;
      case "benevolent":
        this.setStrategy(new PlayerBenevolent(name));
        break;
      case "cheater":
        this.setStrategy(new PlayerCheater(name));
        break;
      case "human":
        this.setStrategy(new PlayerHuman(name));
        break;
    }
  }

  /**
   * This method returns all countries owned by a player.
   *
   * @param playerName The name of the player.
   * @param gameMap the entire map graph
   * @return a list of countries owned by this player.
   */
  public static ArrayList<Country> getCountriesByOwnership(String playerName, GameMap gameMap) {
    return gameMap.getCountries().values().stream()
        .filter(c -> c.getOwnerName().equals(playerName))
        .collect(toCollection(ArrayList::new));
  }

  /**
   * Utility method to check whether the player has lost the game.
   *
   * @param playerName String with the player's name.
   * @param gameMap The GameMap object.
   * @return boolean true if player is still in the game, false otherwise.
   */
  public static boolean checkPlayerOwnsAtleastOneCountry(String playerName, GameMap gameMap) {
    return getCountriesByOwnership(playerName, gameMap).size() > 0;
  }

  /**
   * Utility method to check whether the player owns all the countries
   *
   * @param playerName String with the player's name.
   * @param gameMap The GameMap object.
   * @return boolean true if player is still in the game, false otherwise.
   */
  public static boolean checkPlayerOwnsAllTheCountries(String playerName, GameMap gameMap) {
    return getCountriesByOwnership(playerName, gameMap).size() == gameMap.getCountries().size();
  }

  /**
   * Calculates bonus armies if a player owns a continent
   *
   * @param playerName current player name
   * @param gameMap contains map state
   * @return bonus armies
   */
  public static int getBonusArmiesIfPlayerOwnsContinents(String playerName, GameMap gameMap) {
    int bonusArmies = 0;
    Map<String, List<Country>> mapByContinents =
        gameMap.getCountries().values().stream().collect(groupingBy(Country::getContinent));
    Map<String, List<Country>> mapByPlayersOwnership =
        gameMap.getCountries().values().stream()
            .filter(c -> c.getOwnerName().equals(playerName))
            .collect(groupingBy(Country::getContinent));
    Map<String, Integer> continentsSize =
        mapByContinents.entrySet().stream()
            .map(e -> new AbstractMap.SimpleEntry<String, Integer>(e.getKey(), e.getValue().size()))
            .collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    Map<String, Integer> continentsOwnership =
        mapByPlayersOwnership.entrySet().stream()
            .map(e -> new AbstractMap.SimpleEntry<String, Integer>(e.getKey(), e.getValue().size()))
            .collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    for (Map.Entry<String, Integer> entry : continentsOwnership.entrySet()) {
      int fullOwnerShip = continentsSize.get(entry.getKey());
      int currentOwnerShip = entry.getValue();
      if (fullOwnerShip == currentOwnerShip) {
        int controlValue = gameMap.getContinents().get(entry.getKey()).getValue();
        bonusArmies += controlValue;
      }
    }
    return bonusArmies;
  }

  /**
   * Calculate armies for reinforcement phase
   *
   * @param gameMap contains map state
   * @return armies count
   */
  public static int calculateReinforcements(GameMap gameMap) {
    String playerName = gameMap.getCurrentPlayer().strategy.getPlayerName();
    int ownedCountries = getCountriesByOwnership(playerName, gameMap).size();
    int allReinforcementArmies = getBonusArmiesIfPlayerOwnsContinents(playerName, gameMap);

    if (ownedCountries < 9) {
      allReinforcementArmies += 3;
    }
    allReinforcementArmies += ownedCountries / 3;
    return allReinforcementArmies;
  }

  /**
   * Utility to get indices for AI players.
   *
   * @return an integer array of indices.
   */
  public static int[] getCardExchangeIndices(ArrayList<Card> cardsInHand) {
    ArrayList<Integer> infantryList = new ArrayList<>();
    ArrayList<Integer> cavalryList = new ArrayList<>();
    ArrayList<Integer> artilleryList = new ArrayList<>();
    int iteratorIndex = 0;
    for (Card card : cardsInHand) {
      switch (card.getType().name()) {
        case "INFANTRY":
          infantryList.add(iteratorIndex);
          break;
        case "CAVALRY":
          cavalryList.add(iteratorIndex);
          break;
        case "ARTILLERY":
          artilleryList.add(iteratorIndex);
          break;
      }
      iteratorIndex++;
    }

    if (infantryList.isEmpty() || cavalryList.isEmpty() || artilleryList.isEmpty()) {
      if (infantryList.size() > 2) return infantryList.stream().mapToInt(i -> i).toArray();
      if (cavalryList.size() > 2) return cavalryList.stream().mapToInt(i -> i).toArray();
      if (artilleryList.size() > 2) return artilleryList.stream().mapToInt(i -> i).toArray();
    }

    return new int[] {infantryList.get(0), cavalryList.get(0), artilleryList.get(0)};
  }

  /**
   * Gets the current strategy for the player.
   *
   * @return a PlayerStrategy object
   */
  public PlayerStrategy getStrategy() {
    return strategy;
  }

  /**
   * Sets the current strategy for the player.
   *
   * @param strategy the PlayerStrategy to use.
   */
  public void setStrategy(PlayerStrategy strategy) {
    this.strategy = strategy;
  }

  /**
   * Executes add card for this player strategy.
   *
   * @param card The Card object to be added.
   */
  public void addCard(Card card) {
    strategy.addCard(card);
  }

  /**
   * Executes attack using the current strategy.
   *
   * @param gameMap The Game Map instance.
   * @param command The command string.
   */
  public void attack(GameMap gameMap, String command) {
    strategy.attack(gameMap, command);
  }

  /**
   * Executes reinforce using the current strategy.
   *
   * @param countryToPlace name of country
   * @param armiesToPlace count of armies to place
   * @param gameMap the Game Map instance
   */
  public void reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace) {
    strategy.reinforce(gameMap, countryToPlace, armiesToPlace);
  }

  /**
   * Executes fortify using the current strategy.
   *
   * @param gameMap the Game Map instance
   * @param fromCountry country name to move from
   * @param toCountry contry name to move to
   * @param armyToMove no of armies to move
   */
  public void fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove) {
    strategy.fortify(gameMap, fromCountry, toCountry, armyToMove);
  }
}
