package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * This is the Player class which handles every player.
 * 
 * @author s_anakih
 *
 */
public class Player {

  /**
   * This instance variable holds the name of the player.
   */
  private String playerName;

  /**
   * This constructor initializes the class.
   * 
   * @param playerName
   */
  public Player(String playerName) {
    super();
    this.playerName = playerName;
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
   * This is an override for pretty printing the name.
   */
  @Override
  public String toString() {
    return String.format("%s", this.playerName);
  }

  /**
   * Validates the count of players in the game.
   * 
   * @param playerNames a list of names of every player
   * @param countriesSize the size of the countries list.
   * @return
   */
  public static boolean validatePlayerCount(ArrayList<String> playerNames, int countriesSize) {
    if (playerNames.size() <= 1) {
      System.out.println("There should be at least two players to play.");
      return true;
    } else if (playerNames.size() > countriesSize) {
      System.out.println("The player count exceeds the number of countries in the map.");
    }
    return false;
  }

  /**
   * This processes the addplayer command.
   * 
   * @param commands an arraylist of options
   * @param playersList the list of players
   */
  public static void addOrRemovePlayer(ArrayList<String> commands, ArrayList<Player> playersList) {
    for (String command : commands) {
      String[] commandSplit = command.split(" ");
      Player p = new Player(commandSplit[1]);
      switch (commandSplit[0]) {
        case "add":
          if (!playersList.contains(p)) {
            playersList.add(p);
            System.out.println("added" + p.getPlayerName());
          } else
            System.out.println(commandSplit[1] + " Player already exists");
          break;
        case "remove":
          if (playersList.contains(p)) {
            playersList.remove(p);
            System.out.println("removed" + p.getPlayerName());
          } else
            System.out.println(commandSplit[1] + " Player does not exist");
      }
      break;
    }
  }

  /**
   * This method randomly populates all the countries on the map.
   * 
   * @param playersList a list of players
   * @param gameMap the entire map graph
   * @return a list of countries with ownership
   */
  public static ArrayList<Country> populateCountries(ArrayList<Player> playersList,
      GameMap gameMap) {
    ArrayList<Country> countries =
        gameMap.getCountries().values().stream().collect(Collectors.toCollection(ArrayList::new));
    Collections.shuffle(countries);
    int countrySize = countries.size();
    int playerCount = playersList.size();
    for (int i = 0; i <= countrySize; i++) {
      countries.get(i).setOwnerName(playersList.get(i % playerCount).getPlayerName());
    }
    return countries;
  }

  /**
   * This method returns all countries owned by a player.
   * 
   * @param playerName The name of the player.
   * @param gameMap the entire map graph
   * @return a list of countries owned by this player.
   */
  public static ArrayList<Country> getCountriesByOwnership(String playerName, GameMap gameMap) {
    ArrayList<Country> countries =
        gameMap.getCountries().values().stream().filter(c -> c.getOwnerName().equals(playerName))
            .collect(Collectors.toCollection(ArrayList::new));
    return countries;
  }
}

