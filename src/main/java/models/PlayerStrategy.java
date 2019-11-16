package models;

import java.util.*;
import java.util.stream.Collectors;

import static views.ConsoleView.display;

/**
 * This interface holds the methods for implementing a Player Strategy
 */
public interface PlayerStrategy {

  /**
   * The attack command allows the user to attack another territory.
   * @param gameMap the GameMap instance
   * @param command the command entered by the user
   * @return boolean if the command was successful or not
   */
  boolean attack(GameMap gameMap, String command);

  /**
   * The reinforce command allows the user to reinforce their territories.
   * @param gameMap the GameMap instance
   * @param countryToPlace the country to reinforce
   * @param armiesToPlace the number of armies
   * @return boolean if the command was successful or not
   */
  boolean reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace);

  /**
   * The fortify command allows the user to fortify their territories.
   * @param gameMap the GameMap instance
   * @param fromCountry the country to move from
   * @param toCountry the country to move to
   * @param armyToMove the number of armies
   * @return boolean if the command was successful or not.
   */
  boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove);

  default void exchangeCardsForArmies(int[] indices) {
    // TODO
  }
}
