package models;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

	/**
	 * Stores the number of armies a player has.
	 */
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
	 * Utility method to check whether the player has lost the game.
	 *
	 * @param playerName String with the player's name.
	 * @param gameMap    The GameMap object.
	 * @return boolean true if player is still in the game, false otherwise.
	 */
	public static boolean checkPlayerOwnsAtleastOneCountry(String playerName, GameMap gameMap) {
		return getCountriesByOwnership(playerName, gameMap).size() > 0;
	}

  /**
   * Utility method to check whether the player owns all the countries
   *
   * @param playerName String with the player's name.
   * @param gameMap    The GameMap object.
   * @return boolean true if player is still in the game, false otherwise.
   */
  public static boolean checkPlayerOwnsAllTheCountries(String playerName, GameMap gameMap) {
    return getCountriesByOwnership(playerName, gameMap).size() == gameMap.getCountries().size();
  }

	/**
	 * Calculates bonus armies if a player owns a continent
	 *
	 * @param playerName current player name
	 * @param gameMap    contains map state
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
						.collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));
		Map<String, Integer> continentsOwnership =
				mapByPlayersOwnership.entrySet().stream()
						.map(e -> new AbstractMap.SimpleEntry<String, Integer>(e.getKey(), e.getValue().size()))
						.collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));
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
	public int calculateReinforcements(GameMap gameMap) {
		int ownedCountries = getCountriesByOwnership(this.playerName, gameMap).size();
		int allReinforcementArmies = getBonusArmiesIfPlayerOwnsContinents(playerName, gameMap);

		if (ownedCountries < 9) {
			allReinforcementArmies += 3;
		}
		allReinforcementArmies += ownedCountries / 3;
		return allReinforcementArmies;
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
	 * Getter for number of armies the player owns.
	 *
	 * @return int with number of armies
	 */
	public int getNumberOfArmies() {
		return numberOfArmies;
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

	/**
	 * Check whether one Player object is equal to another.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Player player = (Player) o;
		return numberOfArmies == player.numberOfArmies && playerName.equals(player.playerName);
	}

	/**
	 * @return int hash code of the Player object.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(playerName, numberOfArmies);
	}
}
