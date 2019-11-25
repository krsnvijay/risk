package utils;

import models.Card;
import models.GameMap;
import models.player.Player;
import models.player.PlayerStrategy;
import models.state.PlayerState;

import java.util.ArrayList;

public class ConcreteGameStateBuilder extends GameStateBuilder {
  public ConcreteGameStateBuilder() {
    createGameState();
  }

  @Override
  void buildContinents(GameMap gameMap) {
    gameState.setContinents(gameMap.getContinents());
  }

  @Override
  void buildCountries(GameMap gameMap) {
    gameState.setCountries(gameMap.getCountries());
  }

  @Override
  void buildCurrentContext(GameMap gameMap) {
    gameState.setCurrentContext(gameMap.getCurrentContext());
  }

  @Override
  void buildCurrentPlayerIndex(GameMap gameMap) {
    gameState.setCurrentPlayerIndex(GameMap.getCurrentPlayerIndex());
  }

  @Override
  void buildNumberOfTradedSet(GameMap gameMap) {
    gameState.setNumberOfTradedSet(GameMap.getNumberOfTradedSet());
  }

  @Override
  void buildArmiesTradedForSet(GameMap gameMap) {
    gameState.setArmiesTradedForSet(GameMap.getArmiesTradedForSet());
  }

  @Override
  void buildCardCount(GameMap gameMap) {
    gameState.setCardCount(Card.getCardCount());
  }

  @Override
  void buildBorders(GameMap gameMap) {
    gameState.setBorders(gameMap.getBorders());
  }

  @Override
  void buildPlayersList(GameMap gameMap) {
    ArrayList<PlayerState> playerStates = new ArrayList<>();
    for (Player player : gameMap.getPlayersList()) {
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
  void buildDeck(GameMap gameMap) {
    gameState.setDeck(gameMap.getDeck());
  }
}
