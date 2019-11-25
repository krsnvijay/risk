package models.player;

import models.Context;
import models.GameMap;
import org.junit.Before;
import org.junit.Test;
import utils.DominationMapParser;
import utils.MapParser;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

/** test class to check the functionality of PlayerAggresive.java
 * {@link PlayerAggressive}
 * @author Siddharth Singh
 */
public class PlayerAggressiveTest {
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
    gameMap.addGamePlayer(PLAYER_1, "aggressive");
    gameMap.addGamePlayer(PLAYER_2, "human");
    gameMap.setCurrentContext(Context.GAME_SETUP);
    gameMap.gameSetup();
    gameMap.placeAll();
    GameMap.setCurrentPlayerIndex(0);
  }

  @Test
  public void attack() {}

  @Test
  public void reinforce() {}

  @Test
  public void fortify() {}

  @Test
  public void exchangeCardsForArmies() {}
}