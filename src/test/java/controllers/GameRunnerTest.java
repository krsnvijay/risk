package controllers;

import org.junit.Before;
import org.junit.Test;

import models.GameMap;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * test class to check the functionalities of GameRunner.java
 * @see controllers.GameRunner
 * 
 */

public class GameRunnerTest {

	/** 
     * players in the game should either be more than 2 and less than 6
     * 
     */

	String reason;
    ArrayList<String> playersList = new ArrayList<>();
    boolean result;
    
	@Before
	public void setUp() {
		
	}
	
  @Test
  public void validatePlayerCount() {
	  
	  //Arrange
	  playersList.add("player1");
	  //Act
	  result = GameRunner.validatePlayerCount(playersList);
	  reason = "Player count should be greater than 2";
	  //Assert
	  assertFalse(reason, result);
    
	  
	  //Arrange
	  playersList.add("player2");
	  //Act
	  if (GameRunner.validatePlayerCount(playersList)) 
		  result = true;
	  else 
		  result = false;
	  //Assert
	  assertTrue(result);
    
	  //Arrange
	  playersList.add("player3");
	  playersList.add("player4");
	  playersList.add("player5");
	  playersList.add("player6");
	  //Act
	  result = GameRunner.validatePlayerCount(playersList);
	  //Assert
	  assertTrue(result);
	  
	  //Arrange
	  playersList.add("player7");
	  //Act
	  result = GameRunner.validatePlayerCount(playersList);
	  reason = "Player count should be less than 6";
	  //Assert
	  assertFalse(reason, result);
  }

}