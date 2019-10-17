package models;

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import utils.MapParser;

public class PlayerTest {

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
		countries = new ArrayList<>(gameMap.getCountries().values());
	}
 @Test
  public void getCountriesByOwnership() {
	  	playerName = player1.getPlayerName();
   gameMap.setCountries(gameMap.populateCountries(playersList));
		ArrayList<Country> noOfCountries = new ArrayList<Country>();
		noOfCountries = Player.getCountriesByOwnership(playerName, gameMap);
		int ownedCountries = Player.getCountriesByOwnership("Player2", gameMap).size();
		reason = "Number of countries should be zero";
		assertEquals(reason,21,ownedCountries);
  }
  
 @Test
  public void calculateReinforcements() {
   gameMap.setCountries(gameMap.populateCountries(playersList));
	 	int ownedCountries = Player.getCountriesByOwnership("Player1", gameMap).size();
   int ownedContinents = Player.getBonusArmiesIfPlayerOwnsContinents("Player1", gameMap);
	 int expectedReinforcementArmies = ownedContinents + (ownedCountries / 3);
	 	int actualReinforcementArmies = player1.calculateReinforcements(gameMap);
	 	reason = "Number of reinforcement armies should be "+ expectedReinforcementArmies;
	 	assertEquals(reason,expectedReinforcementArmies,actualReinforcementArmies);	 	
  }
 
 @Test
 public void checkBonusArmiesForContinent() {
	 	countries = countries.stream().sorted((c1,c2)->c1.getContinent().compareTo(c2.getContinent())).collect(Collectors.toCollection(ArrayList::new));
		for (int i = 0; i < 9; i++) {
			countries.get(i).setOwnerName("Player2");
		}
		Map<String, Country> map1 = countries.stream().limit(9).collect(toMap(Country::getName, c -> c));
		Map<String, Country> map2 = populateCountriesVariant(playersList, countries);
		Stream<Entry<String, Country>> combined = Stream.concat(map1.entrySet().stream(), map2.entrySet().stream());
		Map<String, Country> combinedMap = combined.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		gameMap.setCountries(combinedMap);
		int actualBonusArmies = Player.getBonusArmiesIfPlayerOwnsContinents("Player2", gameMap);
		int expectedBonusArmies = 3;
		reason = "Numer of bonus armies expected is "+expectedBonusArmies;
		assertEquals(reason,expectedBonusArmies,actualBonusArmies);
 }
 
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