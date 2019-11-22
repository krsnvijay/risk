package models;

import models.player.Player;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 * This model holds all the information for the World Domination view.
 *
 * @author Warren
 * @version 1.0
 */
public class WorldDomination extends Observable {
  /** Instance of the WorldDomination class. */
  private static WorldDomination worldDomination = null;

  /** Instance of the GameMap. */
  private GameMap gameMap = GameMap.getGameMap();

  /** Holds the ownership percentage. */
  private Map<String, Double> playerOwnershipPercentage = new HashMap<>();

  /** Holds the total armies owned by the players. */
  private Map<String, Integer> totalArmiesOwnedByPlayer = new HashMap<>();

  /** Holds the continents owned by players. */
  private Map<String, ArrayList<String>> continentsOwnedByPlayers = new HashMap<>();

  /**
   * Gets an instance of this class.
   *
   * @return The instance of WorldDomination object.
   */
  public static WorldDomination getInstance() {
    if (worldDomination == null) {
      worldDomination = new WorldDomination();
    }
    return worldDomination;
  }

  /** Refreshes the attributes and notifies the observers to update the WD View. */
  public void recomputeAttributes() {
    List<Context> excludedContexts =
        Arrays.asList(Context.MAIN_MENU, Context.MAP_EDITOR, Context.GAME_SETUP);
    if (!excludedContexts.contains(gameMap.getCurrentContext())) {
      worldDomination.computeArmyCounts();
      worldDomination.computeContinentsOwnedByPlayers();
      worldDomination.computeOwnershipPercentage();
      setChanged();
      notifyObservers();
    }
  }

  /** Calculates the continents owned by each player. */
  private void computeContinentsOwnedByPlayers() {
    Map<String, List<Country>> mapByContinents =
        gameMap.getCountries().values().stream().collect(groupingBy(Country::getContinent));
    Map<String, Integer> continentsSize =
        mapByContinents.entrySet().stream()
            .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().size()))
            .collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

    continentsOwnedByPlayers =
        gameMap.playersList.stream()
            .map(
                player -> {
                  ArrayList<String> continentsOwnedByPlayer = new ArrayList<>();

                  Map<String, List<Country>> mapByPlayersOwnership =
                      gameMap.getCountries().values().stream()
                          .filter(
                              c -> c.getOwnerName().equals(player.getStrategy().getPlayerName()))
                          .collect(groupingBy(Country::getContinent));

                  Map<String, Integer> continentsOwnership =
                      mapByPlayersOwnership.entrySet().stream()
                          .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().size()))
                          .collect(
                              toMap(
                                  AbstractMap.SimpleEntry::getKey,
                                  AbstractMap.SimpleEntry::getValue));

                  for (Map.Entry<String, Integer> entry : continentsOwnership.entrySet()) {
                    int fullOwnerShip = continentsSize.get(entry.getKey());
                    int currentOwnerShip = entry.getValue();
                    if (fullOwnerShip == currentOwnerShip) {
                      continentsOwnedByPlayer.add(entry.getKey());
                    }
                  }

                  return new AbstractMap.SimpleEntry<>(
                      player.getStrategy().getPlayerName(), continentsOwnedByPlayer);
                })
            .collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
  }

  /**
   * Gets the continents owned by players.
   *
   * @return a Map with the data.
   */
  public Map<String, ArrayList<String>> getContinentsOwnedByPlayers() {
    computeContinentsOwnedByPlayers();
    return continentsOwnedByPlayers;
  }

  /** Calculates the army counts for each player. */
  private void computeArmyCounts() {
    totalArmiesOwnedByPlayer =
        gameMap.playersList.stream()
            .map(
                player ->
                    new AbstractMap.SimpleEntry<>(
                        player.getStrategy().getPlayerName(),
                        Player.getCountriesByOwnership(
                                player.getStrategy().getPlayerName(), gameMap)
                            .stream()
                            .mapToInt(Country::getNumberOfArmies)
                            .sum()))
            .collect(
                Collectors.toMap(
                    AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
  }

  /**
   * Calculates the total armies owned by players.
   *
   * @return a Map object with the data.
   */
  public Map<String, Integer> getTotalArmiesOwnedByPlayer() {
    computeArmyCounts();
    return totalArmiesOwnedByPlayer;
  }

  /** Calculates the ownership percentage for players. */
  private void computeOwnershipPercentage() {
    double totalNumOfCountries = gameMap.getCountries().size();
    playerOwnershipPercentage =
        gameMap.playersList.stream()
            .map(
                player -> {
                  long ownershipCount =
                      Player.getCountriesByOwnership(player.getStrategy().getPlayerName(), gameMap)
                          .stream()
                          .count();
                  return new AbstractMap.SimpleEntry<>(
                      player.getStrategy().getPlayerName(),
                      (ownershipCount / totalNumOfCountries) * 100);
                })
            .collect(
                Collectors.toMap(
                    AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
  }

  /**
   * Returns the ownership percentage of players.
   *
   * @return a Map containing the data.
   */
  public Map<String, Double> getPlayerOwnerShipPercentage() {
    computeOwnershipPercentage();
    return playerOwnershipPercentage;
  }
}
