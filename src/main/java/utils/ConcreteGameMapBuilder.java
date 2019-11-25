package utils;

import models.Card;
import models.GameMap;
import models.player.Player;
import models.state.GameState;
import models.state.PlayerState;

import java.util.ArrayList;

public class ConcreteGameMapBuilder extends GameMapBuilder {
  @Override
  void buildPlayersList(GameState gameState) {
    ArrayList<Player> players = new ArrayList<>();
    for (PlayerState playerState : gameState.getPlayersList()) {
      Player p = new Player(playerState.getPlayerName(), playerState.getStrategy());
      p.getStrategy().setNumberOfArmies(playerState.getNumberOfArmies());
      p.getStrategy().setCardsInHand(playerState.getCardsInHand());
      p.getStrategy().setNumberOfArmies(playerState.getNumberOfArmies());
      players.add(p);
    }
    getGameMap().setPlayersList(players);
  }

  @Override
  void buildDeck(GameState gameState) {
    getGameMap().setDeck(gameState.getDeck());
  }

  @Override
  void buildBorders(GameState gameState) {
    getGameMap().setBorders(gameState.getBorders());
  }

  @Override
  void buildContinents(GameState gameState) {
    getGameMap().setContinents(gameState.getContinents());
  }

  @Override
  void buildCountries(GameState gameState) {
    getGameMap().setCountries(gameState.getCountries());
  }

  @Override
  void buildCurrentContext(GameState gameState) {
    getGameMap().setCurrentContext(gameState.getCurrentContext());
  }

  @Override
  void buildCurrentPlayerIndex(GameState gameState) {
    GameMap.setCurrentPlayerIndex(gameState.getCurrentPlayerIndex());
  }

  @Override
  void buildNumberOfTradedSet(GameState gameState) {
    GameMap.setNumberOfTradedSet(gameState.getNumberOfTradedSet());
  }

  @Override
  void buildArmiesTradedForSet(GameState gameState) {
    GameMap.setArmiesTradedForSet(gameState.getArmiesTradedForSet());
  }

  @Override
  void buildCardCount(GameState gameState) {
    Card.setCardCount(gameState.getCardCount());
  }
}
