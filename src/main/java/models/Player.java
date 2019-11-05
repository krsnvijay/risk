package models;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import static java.util.stream.Collectors.*;
import static views.ConsoleView.display;

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
	private int numberOfArmies;

	public void setCardsInHand(List<Card> cardsInHand) {
		this.cardsInHand = cardsInHand;
	}

	/**
	 * Stores the cards currently held by the player.
	 */
	private List<Card> cardsInHand = new ArrayList<>();

	/** Maintains the number of sets traded in game*/
	private static int numberOfTradedSet = 0;

	/** Number of armies traded in for each set*/
	private static int armiesTradedForSet = 0;

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
	 * Returns the player's hand.
	 *
	 * @return List with the Cards
	 */
	public List<Card> getCardsInHand() {
		return cardsInHand;
	}

	/**
	 * Adds a card to this player's hand.
	 *
	 * @param card The Card object to be added.
	 */
	public void addCard(Card card) {
		this.cardsInHand.add(card);
	}

	/**
	 * Exchange the card for armies.
	 *
	 * @param indices the positions of the cards in the list.
	 */
	public void exchangeCardsForArmies(int[] indices) {
		Set<String> cardSet = new HashSet<>();
		for(int index: indices) {
			if(index >= 0 && index < cardsInHand.size() ) {
					cardSet.add(cardsInHand.get(index).getCardValue());
			} else{
				display("One OR more of your card indices are INCORRECT", false);
				break;
			}
		}
		if(cardSet.size() == 1 || cardSet.size() == 3){
			numberOfTradedSet++;
			int armiesAcquired = giveArmies();
			numberOfArmies += armiesAcquired;
			for(int index: indices){
                cardsInHand.remove(index);
            }
			display("Acquired " + armiesAcquired + " through card exchange", false);
		}
		else{
			display("The set provided is not valid. Valid set: 3 cards of same type or 3 cards of different type", false);
		}
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

	/**
	 * This method gives armies to the player
	 *
	 */
	public int giveArmies() {
		if(numberOfTradedSet == 1)
			armiesTradedForSet += 4;
		else if(numberOfTradedSet <6)
			armiesTradedForSet += 2;
		else if(numberOfTradedSet == 6)
			armiesTradedForSet += 3;
		else
			armiesTradedForSet += 5;

		return armiesTradedForSet;
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
