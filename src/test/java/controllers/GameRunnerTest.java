package controllers;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GameRunnerTest {

  @Test
  public void validatePlayerCount() {
    String reason;
    ArrayList<String> playersList = new ArrayList<>();
    playersList.add("player1");
    boolean result = GameRunner.validatePlayerCount(playersList);
    reason = "Player count should be greater than 2";
    assertFalse(reason, result);
    playersList.add("player2");
    boolean result1;
    if (GameRunner.validatePlayerCount(playersList)) result1 = true;
    else result1 = false;
    assertTrue(result1);
    playersList.add("player3");
    playersList.add("player4");
    playersList.add("player5");
    playersList.add("player6");

    boolean result2 = GameRunner.validatePlayerCount(playersList);
    assertTrue(result2);
    playersList.add("player7");
    boolean result3 = GameRunner.validatePlayerCount(playersList);
    reason = "Player count should be less than 6";
    assertFalse(reason, result3);
  }

}