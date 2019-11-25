package utils;

import models.GameMap;
import models.player.Player;
import models.player.PlayerStrategy;
import models.state.PlayerState;

import java.util.ArrayList;

public class RiskGameStateBuilder extends GameStateBuilder {
  public RiskGameStateBuilder() {
    createGameState();
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
  void buildCurrentContext() {
    gameState.setCurrentContext(GameMap.getGameMap().getCurrentContext());
  }

  @Override
  void buildCurrentPlayerIndex() {
    gameState.setCurrentPlayerIndex(GameMap.getCurrentPlayerIndex());
  }

  @Override
  void buildNumberOfTradedSet() {}

  @Override
  void buildArmiesTradedForSet() {}

  @Override
  void buildBorders() {
    gameState.setBorders(GameMap.getGameMap().getBorders());
  }

  @Override
  void buildPlayersList() {
    ArrayList<PlayerState> playerStates = new ArrayList<>();
    for (Player player : GameMap.getGameMap().getPlayersList()) {
      PlayerStrategy playerStrategy = player.getStrategy();
      PlayerState playerState =
          PlayerState.PlayerStateBuilder.aPlayerState()
              .withPlayerName(playerStrategy.getPlayerName())
              .withCardsInHand(playerStrategy.getCardsInHand())
              .withNumberOfArmies(playerStrategy.getNumberOfArmies())
              .withStrategy(playerStrategy.getStrategyType())
              .build();
      playerStates.add(playerState);
    }
    gameState.setPlayersList(playerStates);
  }

  @Override
  void buildDeck() {
    gameState.setDeck(GameMap.getGameMap().getDeck());
  }
}
