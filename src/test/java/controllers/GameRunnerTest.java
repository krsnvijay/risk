package controllers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import models.GameMap;
import org.junit.Test;

public class GameRunnerTest {

  @Test
  public void validatePlayerCount() {
    String reason;
    GameMap gameMap = new GameMap();
    gameMap.addGamePlayer("player1");
    boolean result = gameMap.validatePlayerCount();
    reason = "Player count should be greater than 2";
    assertFalse(reason, result);
    gameMap.addGamePlayer("player2");
    boolean result1;
    result1 = gameMap.validatePlayerCount();
    assertTrue(result1);
    gameMap.addGamePlayer("player3");
    gameMap.addGamePlayer("player4");
    gameMap.addGamePlayer("player5");
    gameMap.addGamePlayer("player6");

    boolean result2 = gameMap.validatePlayerCount();
    assertTrue(result2);
    gameMap.addGamePlayer("player7");
    boolean result3 = gameMap.validatePlayerCount();
    reason = "Player count should be less than 6";
    assertFalse(reason, result3);
  }
}
