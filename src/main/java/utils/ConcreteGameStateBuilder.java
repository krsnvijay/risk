package utils;

import models.Card;
import models.GameMap;
import models.player.Player;
import models.player.PlayerStrategy;
import models.state.PlayerState;

import java.util.ArrayList;

/**
 * This class is a concrete builder for the game's state.
 *
 * @author Vijay
 * @version 1.0
 */
public class ConcreteGameStateBuilder extends GameStateBuilder {

  /**
   * Constructor that initiates the building process.
   */
  public ConcreteGameStateBuilder() {
    createGameState();
  }

  /**
   * Builds the continents.
   *
   * @param gameMap
   */
  @Override
  void buildContinents(GameMap gameMap) {
    gameState.setContinents(gameMap.getContinents());
  }

  /**
   * Builds the countries.
   *
   * @param gameMap
   */
  @Override
  void buildCountries(GameMap gameMap) {
    gameState.setCountries(gameMap.getCountries());
  }

  /**
   * Builds the current context
   *
   * @param gameMap contains game state
   */
  @Override
  void buildCurrentContext(GameMap gameMap) {
    gameState.setCurrentContext(gameMap.getCurrentContext());
  }

  /**
   * Builds current player Index
   * @param gameMap contains game state
   */
  @Override
  void buildCurrentPlayerIndex(GameMap gameMap) {
    gameState.setCurrentPlayerIndex(GameMap.getCurrentPlayerIndex());
  }

  /**
   * Builds number of traded set
   * @param gameMap contains game state
   */
  @Override
  void buildNumberOfTradedSet(GameMap gameMap) {
    gameState.setNumberOfTradedSet(GameMap.getNumberOfTradedSet());
  }

  /**
   * Builds armies traded for set
   * @param gameMap contains game state
   */
  @Override
  void buildArmiesTradedForSet(GameMap gameMap) {
    gameState.setArmiesTradedForSet(GameMap.getArmiesTradedForSet());
  }

  /**
   * Build card count for game map
   * @param gameMap contains gamestate
   */
  @Override
  void buildCardCount(GameMap gameMap) {
    gameState.setCardCount(Card.getCardCount());
  }

  /**
   * builds borders from game map
   * @param gameMap contains game state
   */
  @Override
  void buildBorders(GameMap gameMap) {
    gameState.setBorders(gameMap.getBorders());
  }

  /**
   * builds playerList from the game state
   * @param gameMap contains gamestate
   */
  @Override
  void buildPlayersList(GameMap gameMap) {
    ArrayList<PlayerState> playerStates = new ArrayList<>();
    for (Player player : gameMap.getPlayersList()) {
      PlayerStrategy playerStrategy = player.getStrategy();

      PlayerState playerState = new PlayerState(playerStrategy.getStrategyType(), playerStrategy.getPlayerName(),
          playerStrategy.getNumberOfArmies(), playerStrategy.getCardsInHand());
      playerStates.add(playerState);
    }
    gameState.setPlayersList(playerStates);
  }

  /**
   * Builds deck from game map
   * @param gameMap contains gamestate
   */
  @Override
  void buildDeck(GameMap gameMap) {
    gameState.setDeck(gameMap.getDeck());
  }
}
