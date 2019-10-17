package controllers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import org.junit.Test;

/**
 * test class to check the functionalities of GameRunner.java {@link GameRunner}
 */
public class GameRunnerTest {

  /**
   * players in the game should either be more than 2 and less than 6
   */
  String reason;
  /**
   * list of players
   */
  ArrayList<String> playersList = new ArrayList<>();
  /**
   * store validation result
   */
  boolean result;

  /** Tests validatePlayerCount function */
  @Test
  public void validatePlayerCount() {

    // Arrange
    playersList.add("player1");
    // Act
    result = GameRunner.validatePlayerCount(playersList);
    reason = "Player count should be greater than 2";
    // Assert
    assertFalse(reason, result);

    // Arrange
    playersList.add("player2");
    // Act
    result = GameRunner.validatePlayerCount(playersList);
    // Assert
    assertTrue(result);

    // Arrange
    playersList.add("player3");
    playersList.add("player4");
    playersList.add("player5");
    playersList.add("player6");
    // Act
    result = GameRunner.validatePlayerCount(playersList);
    // Assert
    assertTrue(result);

    // Arrange
    playersList.add("player7");
    // Act
    result = GameRunner.validatePlayerCount(playersList);
    reason = "Player count should be less than 6";
    // Assert
    assertFalse(reason, result);
  }
}
