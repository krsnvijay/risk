package models.player;

import models.Context;
import models.Country;
import models.GameMap;
import org.junit.Before;
import org.junit.Test;
import utils.DominationMapParser;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class PlayerCheaterTest {
  public static final String PLAYER_1 = "Player1";
  public static final String PLAYER_2 = "Player2";
  /** reason for failure */
  String reason;
  /** store game state */
  private GameMap gameMap;
  /** Stores the number of armies a player has. */
  private int numberOfArmies;

  /**
   * Sets up context for the test
   *
   * @throws Exception when map file is invalid
   */
  @Before
  public void setUp() throws Exception {
    File riskMap = new File("src/test/resources/risk.map");
    MapParser mapParser = new DominationMapParser();
    gameMap = mapParser.loadMap(riskMap.getPath());
    reason = "";
    gameMap.setPlayersList(new ArrayList<>());
    gameMap.addGamePlayer(PLAYER_1, "Cheater");
    gameMap.addGamePlayer(PLAYER_2, "Human");
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    gameMap.placeAll();
    GameMap.setCurrentPlayerIndex(0);
  }

  @Test
  public void attack() {
    ArrayList<Country> countries = Player.getCountriesByOwnership(PLAYER_1, gameMap);
    Map<String, Country> allCountries = gameMap.getCountries();
    for(Country cheaterCountry: countries) {
      Set<String> borders = gameMap.getBorders().get(cheaterCountry.getName());
      for(String neighborName : borders) {
        Country neighbor = allCountries.get(neighborName);
        if(!neighbor.getOwnerName().equals(PLAYER_1)) {
          neighbor.setOwnerName(PLAYER_1);
          cheaterCountry.removeArmies(numberOfArmies/2);
          neighbor.setNumberOfArmies(numberOfArmies/2);
        }
      }
    }
  }

  @Test
  public void reinforce() {}

  @Test
  public void fortify() {}

  @Test
  public void exchangeCardsForArmies() {}
}