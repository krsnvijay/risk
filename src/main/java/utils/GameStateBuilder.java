package utils;

import models.GameState;

public abstract class GameStateBuilder {
  protected GameState gameState;

  public GameState getGameState() {
    return gameState;
  }

  public void createGameState() {
    gameState = new GameState();
  }

  abstract void buildContext();

  abstract void buildPlayers();

  abstract void buildCurrentPlayer();

  abstract void buildContinents();

  abstract void buildCountries();

  abstract void buildBorders();

  abstract void buildDeck();
}
