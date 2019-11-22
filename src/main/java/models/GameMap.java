package models;

import models.player.Player;
import models.player.PlayerStrategy;

import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * GameMap stores map data i.e borders, countries, files, continents The class is a singleton.
 *
 * @author Vijay
 * @author Siddhant
 * @version 1.0
 */
public class GameMap extends Observable {

  /** The singleton instance of the game's state */
  public static GameMap gameMap = null;
  /** Maintains whose turn it is (index). */
  private static int currentPlayerIndex = 0;
  /** This maintains a list of players currently in the game. */
  public ArrayList<Player> playersList = new ArrayList<>();
  /** This maintains a log of phase-wise activity in the game */
  public String phaseLog = "";
  /** This maintains a list of RISK cards in the deck. */
  public ArrayList<Card> deck = null;
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
  /** The current phase of the game. */
  private Context currentContext;
  /**
   * random Generator for the game
   */
  private static Random randomGenerator = new Random();

  /**
   * The constructor for the GameMap.
   */
  public GameMap() {
    super();
    this.borders = new HashMap<>();
    this.fileSectionData = new ArrayList<>();
    this.countries = new HashMap<>();
    this.continents = new HashMap<>();
    this.fileName = "";
  }

  public static int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * getter for random generator
   *
   * @return Random object
   */
  public static Random getRandomGenerator() {
    return randomGenerator;
  }

  /**
   * Setter for random generator seed
   *
   * @param randomGeneratorSeed Random object generator seed
   */
  public static void setRandomGenerator(long randomGeneratorSeed) {
    randomGenerator.setSeed(randomGeneratorSeed);
  }

  /**
   * Builds the deck of RISK cards
   *
   * @param gameMap The GameMap object to save.
   */
  public static void buildDeck(GameMap gameMap) {
    ArrayList<Country> countriesInMap = new ArrayList<>(gameMap.getCountries().values());
    ArrayList<Card> cardsInDeck = new ArrayList<>();
    for (Country country : countriesInMap) {
      cardsInDeck.add(new Card(country.getName()));
    }
    Collections.shuffle(cardsInDeck);
    gameMap.setDeck(cardsInDeck);
  }

  /**
   * A method to get the existing instance of gameMap, or creating one if it doesn't exist.
   *
   * @return The instance of the gameMap.
   */
  public static GameMap getGameMap() {
    if (gameMap == null) {
      gameMap = new GameMap();
    }
    return gameMap;
  }

  /**
   * Setter for gamemap instance, used in case a map is loaded from file.
   *
   * @param gameMap contains game state
   */
  public static void modifyInstance(GameMap gameMap) {
    GameMap.gameMap = gameMap;
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
   * Setter for currentPlayerIndex
   *
   * @param currentPlayerIndex index of the player to set to
   */
  public static void setCurrentPlayerIndex(int currentPlayerIndex) {
    GameMap.currentPlayerIndex = currentPlayerIndex;
  }

  /**
   * This is the constructor for the GameMap class.
   *
   * @param fileSectionData Contains the information in the [File] section.
   * @param borders Stores an adjacency list of all borders.
   * @param continents Stores a map of all continents.
   * @param countries Stores a map of all countries.
   * @param fileName contains map name
   */
  public void populateGameMap(
      ArrayList<String> fileSectionData,
      Map<String, Set<String>> borders,
      Map<String, Continent> continents,
      Map<String, Country> countries,
      String fileName) {
    this.fileSectionData = fileSectionData;
    this.borders = borders;
    this.continents = continents;
    this.countries = countries;
    this.fileName = fileName;
  }

  /**
   * Returns the Deck of cards.
   *
   * @return an ArrayList of Card objects -- the Deck.
   */
  public ArrayList<Card> getDeck() {
    return deck;
  }

  /**
   * Sets the deck object.
   *
   * @param deck A collection of Card objects.
   */
  public void setDeck(ArrayList<Card> deck) {
    this.deck = deck;
  }

  /** Updates the current player index (round robin fashion) */
  public void updatePlayerIndex() {
    currentPlayerIndex = (currentPlayerIndex + 1) % playersList.size();
    setChanged();
    notifyObservers("CURRENT_PLAYER");
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
   * Adds a game player to the current state
   *
   * @param playerName player to add
   * @return boolean to indicate status
   */
  public boolean addGamePlayer(String playerName, String strategy) {
    Player player = new Player(playerName, strategy);
    if (playersList.stream().anyMatch(p -> p.getStrategy().getPlayerName().equals(playerName))) {
      return false;
    } else {
      playersList.add(player);
      return true;
    }
  }

  /**
   * Removes a game player from the current state
   *
   * @param playerName player to add
   * @return boolean to indicate status
   */
  public boolean removeGamePlayer(String playerName) {
    Optional<Player> p1 =
        playersList.stream()
            .filter(player -> player.getStrategy().getPlayerName().equals(playerName))
            .findFirst();

    if (p1.isPresent()) {
      playersList.remove(p1.get());
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
      Country currentCountry = countries.get(i);
      PlayerStrategy currentPlayer = playerList.get(i % playerCount).getStrategy();
      currentPlayer.subtractArmies(1);
      currentCountry.addArmies(1);
      currentCountry.setOwnerName(currentPlayer.getPlayerName());
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
      p.getStrategy().setNumberOfArmies(armiesPerPlayer);
    }
    this.setCountries(populateCountries(playersList));
    // assigns countries to cards
    buildDeck(this);
    return true;
  }

  /** Assigns a random card to the player from the deck */
  public void assignCard() {
    Player currentPlayer = getCurrentPlayer();
    if (deck.size() > 0) {
      currentPlayer.addCard(deck.get(0));
      deck.remove(0);
    }
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
    String currentPlayerName = currentPlayer.getStrategy().getPlayerName();
    if (!countries.containsKey(countryName)) {
      return false;
    }
    Country currentCountry = countries.get(countryName);
    if (currentCountry.getOwnerName().equals(currentPlayerName)
        && currentPlayer.getStrategy().getNumberOfArmies() > 0) {
      currentCountry.addArmies(numArmies);
      currentPlayer.getStrategy().subtractArmies(numArmies);
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
    return playersList.stream()
        .map(Player::getStrategy)
        .mapToInt(PlayerStrategy::getNumberOfArmies)
        .allMatch(count -> count == 0);
  }

  /**
   * Places army one at a time randomly to each player owned countries in round robin fashion
   *
   * @return true to indicate status
   */
  public boolean placeAll() {
    for (int turn = 0; turn < playersList.size(); turn++) {
      ArrayList<Country> countriesForPlayer =
          Player.getCountriesByOwnership(getCurrentPlayer().getStrategy().getPlayerName(), this);
      Random randomGen = new Random();
      while (getCurrentPlayer().getStrategy().getNumberOfArmies() > 0) {
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
   * This method shows the map for the fortify and reinforce phases
   *
   * @param currentPlayer name of the current player
   * @return String formatted String showing map ownership by player relevant to fortify/reinforce
   *     phases
   */
  public String showMapByOwnershipByCurrentPlayer(String currentPlayer) {
    ArrayList<Country> countriesOwnedByCurrPlayer =
        this.countries.values().stream()
            .filter(country -> country.getOwnerName().equals(currentPlayer))
            .collect(toCollection(ArrayList::new));
    ArrayList<String> countriesOwnedByCurrPlayerStrings =
        countriesOwnedByCurrPlayer.stream()
            .map(Country::getName)
            .collect(toCollection(ArrayList::new));

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

  /**
   * Returns the current context
   *
   * @return Context the current context
   */
  public Context getCurrentContext() {
    return currentContext;
  }

  /**
   * Sets the current context
   *
   * @param currentContext the context to be set
   */
  public void setCurrentContext(Context currentContext) {
    this.currentContext = currentContext;
    setChanged();
    notifyObservers("CURRENT_CONTEXT");
  }

  /**
   * Returns the messages logged for the phase.
   *
   * @return A string with the log.
   */
  public String getPhaseLog() {
    return phaseLog;
  }

  /**
   * Sets the log.
   *
   * @param phaseLog the String to append to the log.
   * @param flushLog whether the log should be cleared.
   */
  public void setPhaseLog(String phaseLog, boolean flushLog) {
    if (flushLog) {
      this.phaseLog = "";
    } else {
      this.phaseLog += phaseLog + "\n";
    }
    setChanged();
    notifyObservers("PHASE_LOG");
  }
}
