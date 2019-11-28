package utils;

import models.GameMap;
import models.state.GameState;

/** Abstract class for GameStateBuilder that is to be extended by ConcreteGameStateBuilder */
public abstract class GameStateBuilder {
  /** Contains game state that is to be saved */
  protected GameState gameState;

  /**
   * Getter for game state
   *
   * @return gamestate
   */
  public GameState getGameState() {
    return gameState;
  }

  /**
   * Setter for gameState
   *
   * @param gameState contains gamestate
   */
  public void setGameState(GameState gameState) {
    gameState = gameState;
  }

  /** Constructs new game state */
  public void createGameState() {
    gameState = new GameState();
  }

  /**
   * build player list from gamemap
   *
   * @param gameMap contains gamestate
   */
  abstract void buildPlayersList(GameMap gameMap);

  /**
   * build deck from gamemap
   *
   * @param gameMap contains gamestate
   */
  abstract void buildDeck(GameMap gameMap);

  /**
   * build borders from game map
   *
   * @param gameMap contains game state
   */
  abstract void buildBorders(GameMap gameMap);

  /**
   * build continents from gamemap
   *
   * @param gameMap contains game state
   */
  abstract void buildContinents(GameMap gameMap);

  /**
   * build countries from gamemap
   *
   * @param gameMap contains game state
   */
  abstract void buildCountries(GameMap gameMap);

  /**
   * build context from game map
   *
   * @param gameMap contains game state
   */
  abstract void buildCurrentContext(GameMap gameMap);

  /**
   * build current player index from game map
   *
   * @param gameMap contains game state
   */
  abstract void buildCurrentPlayerIndex(GameMap gameMap);

  /**
   * build number of traded set from game
   *
   * @param gameMap contains game state
   */
  abstract void buildNumberOfTradedSet(GameMap gameMap);

  /**
   * build armies traded for set from gamemap
   *
   * @param gameMap contains game state
   */
  abstract void buildArmiesTradedForSet(GameMap gameMap);

  /**
   * build card count from gamemap
   *
   * @param gameMap contains gamestate
   */
  abstract void buildCardCount(GameMap gameMap);
}
