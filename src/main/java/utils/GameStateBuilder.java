package utils;

import models.GameMap;
import models.state.GameState;

public abstract class GameStateBuilder {
  protected GameState gameState;

  public GameState getGameState() {
    return gameState;
  }

  public void setGameState(GameState gameState) {
    gameState = gameState;
  }

  public void createGameState() {
    gameState = new GameState();
  }

  abstract void buildPlayersList(GameMap gameMap);

  abstract void buildDeck(GameMap gameMap);

  abstract void buildBorders(GameMap gameMap);

  abstract void buildContinents(GameMap gameMap);

  abstract void buildCountries(GameMap gameMap);

  abstract void buildCurrentContext(GameMap gameMap);

  abstract void buildCurrentPlayerIndex(GameMap gameMap);

  abstract void buildNumberOfTradedSet(GameMap gameMap);

  abstract void buildArmiesTradedForSet(GameMap gameMap);

  abstract void buildCardCount(GameMap gameMap);
}
