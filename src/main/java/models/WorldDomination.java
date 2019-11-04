package models;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public class WorldDomination extends Observable {
  private static WorldDomination worldDomination = null;
  private GameMap gameMap = GameMap.getGameMap();
  private Map<String, Double> playerOwnershipPercentage = new HashMap<>();
  private Map<String, Integer> totalArmiesOwnedByPlayer = new HashMap<>();
  private Map<String, ArrayList<String>> continentsOwnedByPlayers = new HashMap<>();

  public static WorldDomination getInstance() {
    if (worldDomination == null) {
      worldDomination = new WorldDomination();
    }
    return worldDomination;
  }

  public void recomputeAttributes() {
    List<String> excludedContexts = Arrays.asList("MAIN_MENU", "MAP_EDITOR", "GAME_SETUP");
    if (!excludedContexts.contains(gameMap.getCurrentContext().name())) {
      worldDomination.computeArmyCounts();
      worldDomination.computeContinentsOwnedByPlayers();
      worldDomination.computeOwnershipPercentage();
      setChanged();
      notifyObservers();
    }
  }

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
                          .filter(c -> c.getOwnerName().equals(player.getPlayerName()))
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
                    if (fullOwnerShip == currentOwnerShip)
                      continentsOwnedByPlayer.add(entry.getKey());
                  }

                  return new AbstractMap.SimpleEntry<>(
                      player.getPlayerName(), continentsOwnedByPlayer);
                })
            .collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
  }

  public Map<String, ArrayList<String>> getContinentsOwnedByPlayers() {
    computeContinentsOwnedByPlayers();
    return continentsOwnedByPlayers;
  }

  private void computeArmyCounts() {
    totalArmiesOwnedByPlayer =
        gameMap.playersList.stream()
            .map(
                player ->
                    new AbstractMap.SimpleEntry<>(
                        player.getPlayerName(),
                        Player.getCountriesByOwnership(player.getPlayerName(), gameMap).stream()
                            .mapToInt(Country::getNumberOfArmies)
                            .sum()))
            .collect(
                Collectors.toMap(
                    AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
  }

  public Map<String, Integer> getTotalArmiesOwnedByPlayer() {
    computeArmyCounts();
    return totalArmiesOwnedByPlayer;
  }

  private void computeOwnershipPercentage() {
    double totalNumOfCountries = gameMap.getCountries().size();
    playerOwnershipPercentage =
        gameMap.playersList.stream()
            .map(
                player -> {
                  long ownershipCount =
                      Player.getCountriesByOwnership(player.getPlayerName(), gameMap).stream()
                          .count();
                  return new AbstractMap.SimpleEntry<>(
                      player.getPlayerName(), (ownershipCount / totalNumOfCountries) * 100);
                })
            .collect(
                Collectors.toMap(
                    AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
  }

  public Map<String, Double> getPlayerOwnerShipPercentage() {
    computeOwnershipPercentage();
    return playerOwnershipPercentage;
  }
}
