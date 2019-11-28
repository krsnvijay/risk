package utils;

import models.GameMap;
import models.state.GameState;

/**
 * Director for the GameState class
 * Accepts a builder and constructs the GameState object
 */
public class GameStateDirector {
  /**
   * builder for GameState
   */
  private GameStateBuilder builder;

  /**
   * Set builder for the gameState
   *
   * @param gameStateBuilder builder to be used by the director
   */
  public void setBuilder(GameStateBuilder gameStateBuilder) {
    builder = gameStateBuilder;
  }

  /**
   * Constructs gameState from the builder
   * @param gameMap contains game state
   */
  public void constructGameState(GameMap gameMap) {
    builder.createGameState();
    builder.buildPlayersList(gameMap);
    builder.buildDeck(gameMap);
    builder.buildBorders(gameMap);
    builder.buildContinents(gameMap);
    builder.buildCountries(gameMap);
    builder.buildCurrentContext(gameMap);
    builder.buildCurrentPlayerIndex(gameMap);
    builder.buildNumberOfTradedSet(gameMap);
    builder.buildArmiesTradedForSet(gameMap);
    builder.buildCardCount(gameMap);
  }

  /**
   * Get constructed game state from the builder
   * @return GameState object
   */
  public GameState getGameState() {
    return builder.getGameState();
  }


}
