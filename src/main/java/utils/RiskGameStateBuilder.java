package utils;

import models.GameMap;

public class RiskGameStateBuilder extends GameStateBuilder {
  public RiskGameStateBuilder() {
    createGameState();
  }

  @Override
  void buildPlayers() {
    gameState.setPlayersList(GameMap.getGameMap().getPlayersList());
  }

  @Override
  void buildCurrentPlayer() {
    gameState.setCurrentPlayerIndex(GameMap.getCurrentPlayerIndex());
  }

  @Override
  void buildContext() {
    gameState.setCurrentContext(GameMap.getGameMap().getCurrentContext());
  }

  @Override
  void buildContinents() {
    gameState.setContinents(GameMap.getGameMap().getContinents());
  }

  @Override
  void buildCountries() {

    gameState.setCountries(GameMap.getGameMap().getCountries());
  }

  @Override
  void buildBorders() {
    gameState.setBorders(GameMap.getGameMap().getBorders());
  }

  @Override
  void buildDeck() {
    gameState.setDeck(GameMap.getGameMap().getDeck());
  }
}
