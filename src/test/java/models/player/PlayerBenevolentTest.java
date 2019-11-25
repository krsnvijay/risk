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
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

/** test class to check the functionality of PlayerCheater.java
 * {@link PlayerBenevolent}
 * @author Siddharth Singh
 */
public class PlayerBenevolentTest {
  public static final String PLAYER_1 = "Player1";
  public static final String PLAYER_2 = "Player2";
  /** reason for failure */
  String reason;

  /** store game state */
  private GameMap gameMap;

  /**
   * Sets up context for the test
   *
   * @throws Exception when map file is invalid
   */
  @Before
  public void setUp() throws Exception {
    File riskMap = new File("src/test/resources/simple.map");
    MapParser mapParser = new DominationMapParser();
    gameMap = mapParser.loadMap(riskMap.getPath());
    reason = "";
    gameMap.setPlayersList(new ArrayList<>());
    gameMap.addGamePlayer(PLAYER_1, "benevolent");
    gameMap.addGamePlayer(PLAYER_2, "human");
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    gameMap.placeAll();
    GameMap.setCurrentPlayerIndex(0);
  }

  /**
   * check if player reinforces the weakest country that
   * belongs to Benevolent Player
   */
  @Test
  public void reinforce() {
    Map<String, Country> allCountries = gameMap.getCountries();
    Collection<Country> countries = gameMap.getCountries().values();
    countries.stream()
            .filter(country -> country.getContinent().equals("Africa"))
            .forEach(country -> country.setOwnerName(PLAYER_2));
    countries.stream()
            .filter(country -> country.getContinent().equals("Asia"))
            .forEach(country -> country.setOwnerName(PLAYER_1));

  }

  /**
   * check if player fortifies the weakest country that
   * belongs to Benevolent Player
   */
  @Test
  public void fortify() {}
}