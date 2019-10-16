package models;

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import controllers.GameRunner;
import utils.MapParser;

/**
 * test class to check the functionalities of Player.java
 * {@link Player.java}
 * 
 */

public class PlayerTest {

	private GameRunner gamerunner;
	private GameMap gameMap;
	String playerName;
	String reason;
	private Player player1;
	private Player player2;
	private static ArrayList<Player> playersList = new ArrayList<>();
	ArrayList<Country> countries;
	Map<String, Country> countriesOwnership;
	
	@Before
	public void setUp() throws Exception{
		File riskMap = new File("src/test/resources/risk.map");
		gameMap = MapParser.loadMap(riskMap.getPath());
		reason = "";
		player1 = new Player("Player1");
		player2 = new Player("Player2");
		playersList.add(player1);
		playersList.add(player2);
		gamerunner = new GameRunner(gameMap);
		countries = new ArrayList<>(gameMap.getCountries().values());
	}
	
	/** 
     * check if countries are equally divided among the players
     * 
     */
	
	@Test
	public void getCountriesByOwnership() {
		//Arrange
	  	playerName = player1.getPlayerName();
	  	gameMap.setCountries(gamerunner.populateCountries(playersList));
		ArrayList<Country> noOfCountries = new ArrayList<Country>();
		noOfCountries = Player.getCountriesByOwnership(playerName, gameMap);
		//Act
		int ownedCountries = Player.getCountriesByOwnership("Player2", gameMap).size();
		reason = "Number of countries should be zero";
		//Assert
		assertEquals(reason,21,ownedCountries);
	}
	
	/** 
     * calculate whether the Reinforcement armies are calculated correctly
     * 
     */
  
	@Test
	public void calculateReinforcements() {
		//Arrange
	 	gameMap.setCountries(gamerunner.populateCountries(playersList));
	 	int ownedCountries = Player.getCountriesByOwnership("Player1", gameMap).size();
	 	int expectedReinforcementArmies = (int) Math.floor(ownedCountries/3);
	 	//Act
	 	int actualReinforcementArmies = player1.calculateReinforcements(gameMap);
	 	reason = "Number of reinforcement armies should be "+ expectedReinforcementArmies;
	 	//Assert
	 	assertEquals(reason,expectedReinforcementArmies,actualReinforcementArmies);	 	
	}
 
	/** 
     * calculate whether the bonus armies for players who own a continent is calculated correctly
     * 
     */
	
	@Test
	public void checkBonusArmiesForContinent() {
		//Arrange
	 	countries = countries.stream().sorted((c1,c2)->c1.getContinent().compareTo(c2.getContinent())).collect(Collectors.toCollection(ArrayList::new));
		for (int i = 0; i < 9; i++) {
			countries.get(i).setOwnerName("Player2");
		}
		Map<String, Country> map1 = countries.stream().limit(9).collect(toMap(Country::getName, c -> c));
		Map<String, Country> map2 = populateCountriesVariant(playersList, countries);
		Stream<Entry<String, Country>> combined = Stream.concat(map1.entrySet().stream(), map2.entrySet().stream());
		Map<String, Country> combinedMap = combined.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		gameMap.setCountries(combinedMap);
		//Act
		int actualBonusArmies = Player.getBonusArmiesIfPlayerOwnsContinents("Player2", gameMap);
		int expectedBonusArmies = 3;
		reason = "Numer of bonus armies expected is "+expectedBonusArmies;
		//Assert
		assertEquals(reason,expectedBonusArmies,actualBonusArmies);
		}
 
	/** 
     * another variant of populateCountries to manage the context in this test class
     * 
     */
	
	public Map<String, Country> populateCountriesVariant(ArrayList<Player> playerList, ArrayList<Country> countries) {
		countries = countries.stream().skip(9).collect(Collectors.toCollection(ArrayList::new));
		Collections.shuffle(countries);
		int countrySize = countries.size();
		for (int i = 0; i < countrySize; i++) {
			if(i<14)
				countries.get(i).setOwnerName("Player2");
			else
				countries.get(i).setOwnerName("Player1");
			}
		return countries.stream().collect(toMap(Country::getName, c -> c));
		}
}