package models;

import java.util.ArrayList;

import static java.util.stream.Collectors.toCollection;

/**
 * This is the Player class which handles every player.
 *
 * @author s_anakih
 */
public class Player {

  /**
   * This instance variable holds the name of the player.
   */
  private String playerName;

  private int numberOfArmies = 0;

  /**
   * This constructor initializes the class.
   *
   * @param playerName name of the player
   */
  public Player(String playerName) {
    super();
    this.playerName = playerName;
  }

  /**
   * This method returns all countries owned by a player.
   *
   * @param playerName The name of the player.
   * @param gameMap    the entire map graph
   * @return a list of countries owned by this player.
   */
  public static ArrayList<Country> getCountriesByOwnership(String playerName, GameMap gameMap) {
    return gameMap.getCountries().values().stream()
            .filter(c -> c.getOwnerName().equals(playerName))
            .collect(toCollection(ArrayList::new));
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

  /** This is an override for pretty printing the name. */
  @Override
  public String toString() {
    return String.format("%s", this.playerName);
  }

  public int getNumberOfArmies() {
    return numberOfArmies;
  }

  public void setNumberOfArmies(int numberOfArmies) {
    this.numberOfArmies = numberOfArmies;
  }

  /**
   * This method gives armies to the player
   *
   * @param count armies to add to the player
   */
  public void giveArmies(int count) {
    this.numberOfArmies += count;
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
