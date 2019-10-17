package models;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GameMap stores map data i.e borders, countries, files, continents
 *
 * @author Vijay
 */
public class GameMap {

  /** Contains the information in the [File] section. */
  private ArrayList<String> fileSectionData;

  /** Stores an adjacency list of all borders. */
  private Map<String, Set<String>> borders;

  /** Stores a map of all continents. */
  private Map<String, Continent> continents;

  /** Stores a map of all countries. */
  private Map<String, Country> countries;

  /** The name of the map file. */
  private String fileName;
  /**
   * Maintains whose turn it is (index).
   */
  private static int currentPlayerIndex = 0;
  /** This maintains a list of players currently in the game. */
  public ArrayList<Player> playersList = new ArrayList<>();

  /**
   * This is the constructor for the GameMap class.
   *
   * @param fileSectionData Contains the information in the [File] section.
   * @param borders Stores an adjacency list of all borders.
   * @param continents Stores a map of all continents.
   * @param countries Stores a map of all countries.
   * @param fileName contains map name
   */
  public GameMap(
      ArrayList<String> fileSectionData,
      Map<String, Set<String>> borders,
      Map<String, Continent> continents,
      Map<String, Country> countries,
      String fileName) {
    super();
    this.fileSectionData = fileSectionData;
    this.borders = borders;
    this.continents = continents;
    this.countries = countries;
    this.fileName = fileName;
  }

  public GameMap() {
    this.borders = new HashMap<>();
    this.fileSectionData = new ArrayList<>();
    this.countries = new HashMap<>();
    this.continents = new HashMap<>();
    this.fileName = "";
  }

  /** Updates the current player index (round robin fashion) */
  public void updatePlayerIndex() {
    currentPlayerIndex = (currentPlayerIndex + 1) % playersList.size();
  }

  /**
   * A getter for the current player object.
   *
   * @return Player object, for the player whose turn it is.
   */
  public Player getCurrentPlayer() {
    return playersList.get(currentPlayerIndex);
  }

  /**
   * Displays all the borders of the game map.
   *
   * @param border The adjacency list of each border
   * @return returns a pretty string of borders.
   */
  public static String showBorders(Map.Entry<String, Set<String>> border) {
    return String.format("%s %s", border.getKey(), String.join(" ", border.getValue()));
  }

  /**
   * Displays all the borders of the game map by ownership
   *
   * @param border The adjacency list of each border
   * @return returns a pretty string of borders.
   */
  public String showBorderByOwnerShip(Map.Entry<String, Set<String>> border) {
    String country = this.countries.get(border.getKey()).showCountryByOwnership();
    String neighbors =
        border.getValue().stream()
            .map(this.countries::get)
            .map(Country::showCountryByOwnership)
            .collect(joining(" "));
    return String.format("%s -> %s", country, neighbors);
  }

  /** Displays the map grouped by continents. */
  public void showMapByContinents() {
    Map<String, List<Country>> groupedCountries =
        this.countries.values().stream().collect(groupingBy(Country::getContinent));
    String mapByContinents =
        groupedCountries.entrySet().stream()
            .map(
                entry -> {
                  String sectionHeader = entry.getKey();
                  String sectionData =
                      entry.getValue().stream()
                          .map(Country::getName)
                          .sorted()
                          .collect(joining("\n"));
                  return String.format("[%s]\n%s\n", sectionHeader, sectionData);
                })
            .collect(joining("\n"));
    System.out.println(mapByContinents);
  }

  /**
   * Get all countries in a continent
   *
   * @param continentName name of the continent
   * @return set of countrynames that are part of continent
   */
  public Set<String> getCountriesByContinent(String continentName) {
    return this.countries.values().stream()
        .filter(c -> c.getContinent().equals(continentName))
        .map(Country::getName)
        .collect(toSet());
  }

  /**
   * Add a continent to the gameMap
   *
   * @param continentName continent to add
   * @param value control value of continent
   * @return true when continent is added
   */
  public boolean addContinent(String continentName, int value) {
    Continent continent = new Continent(continentName, value);
    this.continents.put(continentName, continent);
    return true;
  }

  /**
   * Removes a continent and all its countries from the gameMap
   *
   * @param continentName continent to remove
   * @return true if continent is removed
   */
  public boolean removeContinent(String continentName) {
    if (!this.continents.containsKey(continentName)) {
      return false;
    }
    getCountriesByContinent(continentName).forEach(this::removeCountry);
    this.continents.remove(continentName);
    return true;
  }

  /**
   * Adds a country to the game map
   *
   * @param countryName country to add
   * @param continentName continent the country belongs to
   * @return true if country is added
   */
  public boolean addCountry(String countryName, String continentName) {
    if (!this.continents.containsKey(continentName)) {
      return false;
    }
    Country country = new Country(countryName, continentName);
    this.countries.put(countryName, country);
    this.borders.put(countryName, new HashSet<>());
    return true;
  }

  /**
   * Removes a country from the game map
   *
   * @param countryName country to remove
   * @return true if country is removed
   */
  public boolean removeCountry(String countryName) {
    if (!this.countries.containsKey(countryName)) {
      return false;
    }
    removeCountryBorders(countryName);
    this.countries.remove(countryName);
    return true;
  }

  /**
   * Adds a border between two countries making them neighbors
   *
   * @param country1 neighboring country 1
   * @param country2 neighboring country 2
   * @return true if border is added
   */
  public boolean addBorder(String country1, String country2) {
    if (!this.countries.containsKey(country1)) {
      System.out.println("Error: The country " + country1 + " does not exist");
      return false;
    }
    if (!this.countries.containsKey(country2)) {
      System.out.println("Error: The country " + country2 + " does not exist");
      return false;
    }
    this.borders.get(country1).add(country2);
    this.borders.get(country2).add(country1);
    return true;
  }

  /**
   * Removes a border between two countries
   *
   * @param country1 neighboring country 1
   * @param country2 neighboring country 2
   * @return true if border is removed
   */
  public boolean removeBorder(String country1, String country2) {
    if (!this.countries.containsKey(country1)) {
      return false;
    }
    if (!this.countries.containsKey(country2)) {
      return false;
    }
    this.borders.get(country1).remove(country2);
    this.borders.get(country2).remove(country1);
    return true;
  }

  /**
   * Removes all borders that a country is part of
   *
   * @param countryName country whose borders are to be removed
   */
  public void removeCountryBorders(String countryName) {
    if (!this.borders.containsKey(countryName)) {
      System.out.println("Error: The country " + countryName + " does not exist");
      return;
    }
    Set<String> neighbors = this.borders.get(countryName);
    for (String neighbor : neighbors) {
      this.borders.get(neighbor).remove(countryName);
      System.out.println("Removed border: " + neighbor + " - " + countryName);
    }
    this.borders.remove(countryName);
  }

  /** Pretty prints the game map. */
  @Override
  public String toString() {
    return String.format(
        "\n[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s",
        this.continents.values().stream().map(Continent::toString).sorted().collect(joining("\n")),
        this.countries.values().stream().map(Country::toString).sorted().collect(joining("\n")),
        this.borders.entrySet().stream().map(GameMap::showBorders).sorted().collect(joining("\n")));
  }

  /** @return String formatted String sorted by ownership. */
  public String showMapByOwnership() {
    return String.format(
        "[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n",
        this.continents.values().stream().map(Continent::toString).sorted().collect(joining("\n")),
        this.countries.values().stream()
            .map(Country::showCountryByOwnership)
            .sorted()
            .collect(joining("\n")),
        this.borders.entrySet().stream()
            .map(this::showBorderByOwnerShip)
            .sorted()
            .collect(joining("\n")));
  }

  /**
   * This method shows the map for the fortify and reinforce phases
   *
   * @return String formatted String
   */
  public String showMapByOwnershipByCurrentPlayer(String currentPlayer) {
    ArrayList<Country> countriesOwnedByCurrPlayer =
        this.countries.values().stream()
            .filter(country -> country.getOwnerName().equals(currentPlayer))
            .collect(Collectors.toCollection(ArrayList::new));
    ArrayList<String> countriesOwnedByCurrPlayerStrings =
        countriesOwnedByCurrPlayer.stream()
            .map(Country::getName)
            .collect(Collectors.toCollection(ArrayList::new));

    String countryListForPlayer =
        countriesOwnedByCurrPlayer.stream()
            .map(
                country -> String.format("%s (%d)", country.getName(), country.getNumberOfArmies()))
            .sorted()
            .collect(joining("\n"));
    String adjacencyListForPlayer =
        this.borders.entrySet().stream()
            .filter(
                borderEntry -> {
                  if (!countriesOwnedByCurrPlayerStrings.contains(borderEntry.getKey())) {
                    return false;
                  }
                  // perform a side effect on it's set to only keep neighbors owned by currPlayer
                  borderEntry.setValue(
                      borderEntry.getValue().stream()
                          .filter(countriesOwnedByCurrPlayerStrings::contains)
                          .collect(toSet()));
                  return true;
                })
            .map(
                borderEntry -> {
                  Country country = this.countries.get(borderEntry.getKey());
                  String countryStr =
                      String.format("%s(%d)", country.getName(), country.getNumberOfArmies());
                  StringBuilder neighborSetStr = new StringBuilder();
                  if (borderEntry.getValue().size() > 0) {
                    for (String neighbor : borderEntry.getValue()) {
                      Country neighborCountry = this.countries.get(neighbor);
                      neighborSetStr.append(
                          String.format(
                              " --> %s(%d)",
                              neighborCountry.getName(), neighborCountry.getNumberOfArmies()));
                    }
                  } else {
                    neighborSetStr =
                        new StringBuilder(
                            "  !! NO NEIGHBORS OF THIS COUNTRY OWNED BY " + currentPlayer);
                  }
                  return countryStr + neighborSetStr.toString();
                })
            .sorted()
            .collect(joining("\n"));
    return String.format(
        "[Countries Owned by %s]\n%s\n\n[Adjacency List for Countries Owned by %s]\n\n%s",
        currentPlayer, countryListForPlayer, currentPlayer, adjacencyListForPlayer);
  }

  /**
   * Adds a game player to the current state
   *
   * @param playerName player to add
   * @return boolean to indicate status
   */
  public boolean addGamePlayer(String playerName) {
    Player player = new Player(playerName);
    if (!playersList.contains(player)) {
      playersList.add(player);
      return true;
    }
    return false;
  }

  /**
   * Removes a game player from the current state
   *
   * @param playerName player to add
   * @return boolean to indicate status
   */
  public boolean removeGamePlayer(String playerName) {
    Player player = new Player(playerName);
    if (playersList.contains(player)) {
      playersList.remove(player);
      return true;
    }
    return false;
  }

  /**
   * This method returns all the borders.
   *
   * @return A Map of all borders in the game map
   */
  public Map<String, Set<String>> getBorders() {
    return borders;
  }

  /**
   * This sets the borders instance variable for the game map class.
   *
   * @param borders A Map, consisting of the country name and the adjacency of borders.
   */
  public void setBorders(Map<String, Set<String>> borders) {
    this.borders = borders;
  }

  /**
   * This method returns a Map object with all the continents.
   *
   * @return A Map object, mapping the name with the Continent object.
   */
  public Map<String, Continent> getContinents() {
    return continents;
  }

  /**
   * This method sets the continents object for the GameMap class.
   *
   * @param continents A Map object, mapping the name with the Continent object.
   */
  public void setContinents(Map<String, Continent> continents) {
    this.continents = continents;
  }

  /**
   * Returns a section parsed from the file.
   *
   * @return A list of raw lines.
   */
  public ArrayList<String> getFileSectionData() {
    return this.fileSectionData;
  }

  /**
   * Sets the fileSectionData variable for the GameMap class.
   *
   * @param fileSectionData An ArrayList with file sections as strings.
   */
  public void setFileSectionData(ArrayList<String> fileSectionData) {
    this.fileSectionData = fileSectionData;
  }

  /**
   * Returns the name of the file.
   *
   * @return the name of the file.
   */
  public String getFileName() {
    return this.fileName;
  }

  /**
   * Sets the name of the map file.
   *
   * @param fileName the name of the file.
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Returns a Map object of countries.
   *
   * @return a Map of countries.
   */
  public Map<String, Country> getCountries() {
    return countries;
  }

  /**
   * Sets the countries instance variable for the GameMap
   *
   * @param countries a Map object of country names and Country objects.
   */
  public void setCountries(Map<String, Country> countries) {
    this.countries = countries;
  }

  /**
   * Gets current playerlist from the game state
   *
   * @return list of players
   */
  public ArrayList<Player> getPlayersList() {
    return playersList;
  }

  /**
   * Sets playerList to the game state
   *
   * @param playersList list of players
   */
  public void setPlayersList(ArrayList<Player> playersList) {
    this.playersList = playersList;
  }

  /**
   * This method randomly populates all the countries on the map.
   *
   * @param playerList list of players in the game
   * @return a map of countries with ownership
   * @author sabari
   */
  public Map<String, Country> populateCountries(ArrayList<Player> playerList) {
    ArrayList<Country> countries = new ArrayList<>(this.countries.values());
    Collections.shuffle(countries);
    int countrySize = countries.size();
    int playerCount = playerList.size();
    for (int i = 0; i < countrySize; i++) {
      countries.get(i).setOwnerName(playerList.get(i % playerCount).getPlayerName());
    }
    return countries.stream().collect(toMap(Country::getName, c -> c));
  }

  /**
   * Validates the count of players in the game.
   *
   * @return A boolean with success or failure.
   */
  public boolean validatePlayerCount() {
    if (playersList.size() <= 1) {
      System.out.println(
          "There should be at least two players to play. Please add more players before loading map & starting game.");
      return false;
    }
    if (playersList.size() > 6) {
      System.out.println(
          "Too many players! Limit is 6 players! Please remove players before loading map & starting game");
      return false;
    }
    return true;
  }

  /**
   * Sets up the army count for each payer, populates countries
   *
   * @return boolean to indicate status
   */
  public boolean gameSetup() {
    int[] totalArmyCounts = {40, 35, 30, 25, 20};
    if (!validatePlayerCount()) {
      return false;
    }
    int armiesPerPlayer = totalArmyCounts[playersList.size() - 2];
    for (Player p : playersList) {
      p.setNumberOfArmies(armiesPerPlayer);
    }
    this.setCountries(populateCountries(playersList));
    return true;
  }

  /**
   * This method places an army in a country that the player owns
   *
   * @param countryName name of the country to place an army
   * @param numArmies armies to place
   * @return A boolean with success or failure.
   */
  public boolean placeArmy(String countryName, int numArmies) {
    Player currentPlayer = getCurrentPlayer();
    String currentPlayerName = currentPlayer.getPlayerName();
    if (!countries.containsKey(countryName)) {
      return false;
    }
    Country currentCountry = countries.get(countryName);
    if (currentCountry.getOwnerName().equals(currentPlayerName)
        && currentPlayer.getNumberOfArmies() > 0) {
      currentCountry.addArmies(numArmies);
      currentPlayer.subtractArmies(numArmies);
      return true;
    }
    return false;
  }

  /**
   * Checks if the setup is complete and the game is ready to begin
   *
   * @return true if numberOfArmies for each player is 0 ; else returns false
   */
  public boolean checkGameReady() {
    return playersList.stream().mapToInt(Player::getNumberOfArmies).allMatch(count -> count == 0);
  }

  /**
   * Places army one at a time randomly to each player owned countries in round robin fashion
   *
   * @return true to indicate status
   */
  public boolean placeAll() {
    for (int turn = 0; turn < playersList.size(); turn++) {
      ArrayList<Country> countriesForPlayer =
          Player.getCountriesByOwnership(getCurrentPlayer().getPlayerName(), this);
      Random randomGen = new Random();
      while (getCurrentPlayer().getNumberOfArmies() > 0) {
        int randomIndexCountry = randomGen.nextInt(countriesForPlayer.size());
        placeArmy(countriesForPlayer.get(randomIndexCountry).getName(), 1);
      }
      if (turn != playersList.size() - 1) {
        updatePlayerIndex();
      }
    }
    return true;
  }

  /**
   * Reinforce a currently owned country with an army
   *
   * @param countryToPlace name of country
   * @param armiesToPlace count of armies to place
   * @return boolean to indicate success or failure
   */
  public boolean reinforce(String countryToPlace, int armiesToPlace) {
    Player currentPlayer = getCurrentPlayer();
    if (Player.getCountriesByOwnership(currentPlayer.getPlayerName(), this).stream()
        .noneMatch(c -> c.getName().equals(countryToPlace))) {
      return false;
    }
    placeArmy(countryToPlace, armiesToPlace);
    return true;
  }

  /**
   * Moves armies from one adjacent country to the other
   *
   * @param fromCountry country name to move from
   * @param toCountry contry name to move to
   * @param armyToMove no of armies to move
   * @return boolean to indicate status
   */
  public boolean fortify(String fromCountry, String toCountry, int armyToMove) {
    boolean result = false;
    boolean isOwnershipValid =
        Player.getCountriesByOwnership(getCurrentPlayer().getPlayerName(), this).stream()
            .anyMatch(c -> c.getName().equals(fromCountry))
            && Player.getCountriesByOwnership(getCurrentPlayer().getPlayerName(), this).stream()
            .anyMatch(c -> c.getName().equals(toCountry));
    if (isOwnershipValid) {
      boolean isAdjacent = borders.get(fromCountry).contains(toCountry);
      if (isAdjacent) {
        boolean isArmyRemoved = countries.get(fromCountry).removeArmies(armyToMove);
        if (isArmyRemoved && armyToMove > 0) {
          countries.get(toCountry).addArmies(armyToMove);
          result = true;
        } else {
          System.out.println("Error number of army(s) is not valid");
        }
      } else {
        System.out.println("Error fromCountry and toCountry are not adjacent");
      }
    } else {
      System.out.println("Error player doesnt own the country or it does not exist in map");
    }
    return result;
  }

  /** Checks whether one GameMap object is equal to another. */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GameMap gameMap = (GameMap) o;
    return fileSectionData.equals(gameMap.fileSectionData)
        && borders.equals(gameMap.borders)
        && continents.equals(gameMap.continents)
        && countries.equals(gameMap.countries)
        && fileName.equals(gameMap.fileName);
  }

  /** @return int hash code for the GameMap object. */
  @Override
  public int hashCode() {
    return Objects.hash(fileSectionData, borders, continents, countries, fileName);
  }
}
