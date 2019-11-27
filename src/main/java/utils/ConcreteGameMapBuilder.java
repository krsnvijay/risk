package utils;

import models.Card;
import models.GameMap;
import models.player.Player;
import models.state.GameState;
import models.state.PlayerState;

import java.util.ArrayList;

/**
 * A concrete builder class for building the GameMap object.
 *
 * @author Vijay
 * @version 1.0
 */
public class ConcreteGameMapBuilder extends GameMapBuilder {

  /**
   * This method builds the Player List.
   *
   * @param gameState The GameState object.
   */
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

  /**
   * This method builds the deck of Card objects.
   *
   * @param gameState The GameState object.
   */
  @Override
  void buildDeck(GameState gameState) {
    getGameMap().setDeck(gameState.getDeck());
  }

  /**
   * This method builds the borders.
   *
   * @param gameState The GameState object.
   */
  @Override
  void buildBorders(GameState gameState) {
    getGameMap().setBorders(gameState.getBorders());
  }

  /**
   * This method builds the continents.
   *
   * @param gameState The GameState object.
   */
  @Override
  void buildContinents(GameState gameState) {
    getGameMap().setContinents(gameState.getContinents());
  }

  /**
   * This method builds the countries.
   *
   * @param gameState The GameState object.
   */
  @Override
  void buildCountries(GameState gameState) {
    getGameMap().setCountries(gameState.getCountries());
  }

  /**
   * This method builds the current context.
   *
   * @param gameState The GameState object.
   */
  @Override
  void buildCurrentContext(GameState gameState) {
    getGameMap().setCurrentContext(gameState.getCurrentContext());
  }

  /**
   * This method builds the current player index.
   *
   * @param gameState The GameState object.
   */
  @Override
  void buildCurrentPlayerIndex(GameState gameState) {
    GameMap.setCurrentPlayerIndex(gameState.getCurrentPlayerIndex());
  }

  /**
   * This method builds the number of traded sets.
   *
   * @param gameState The GameState object.
   */
  @Override
  void buildNumberOfTradedSet(GameState gameState) {
    GameMap.setNumberOfTradedSet(gameState.getNumberOfTradedSet());
  }

  /**
   * This method builds the armies of traded set variable.
   *
   * @param gameState The GameState object.
   */
  @Override
  void buildArmiesTradedForSet(GameState gameState) {
    GameMap.setArmiesTradedForSet(gameState.getArmiesTradedForSet());
  }

  /**
   * This method builds the cardCount variable.
   *
   * @param gameState The GameState object.
   */
  @Override
  void buildCardCount(GameState gameState) {
    Card.setCardCount(gameState.getCardCount());
  }
}
