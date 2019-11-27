package utils;

import models.GameMap;
import models.state.GameState;

/**
 * This abstract class defines the methods for building the GameMap, used for saving the game.
 */
public abstract class GameMapBuilder {
    /**
     * The GameMap object. Accessible to all concrete builders.
     */
    protected GameMap gameMap;

    /**
     * Gets the GameMap object.
     *
     * @return The GameMap object.
     */
    public GameMap getGameMap() {
        return gameMap;
    }

    /**
     * Creates a new GameMap object.
     */
    public void createGameMap() {
        gameMap = new GameMap();
    }

    /**
     * This method builds the list of players.
     *
     * @param gameState The GameState object.
     */
    abstract void buildPlayersList(GameState gameState);

    /**
     * This method builds the deck of cards.
     *
     * @param gameState The GameState object.
     */
    abstract void buildDeck(GameState gameState);

    /**
     * This method builds the borders.
     *
     * @param gameState The GameState object.
     */
    abstract void buildBorders(GameState gameState);

    /**
     * This method builds the continents.
     *
     * @param gameState The GameState object.
     */
    abstract void buildContinents(GameState gameState);

    /**
     * This method builds the countries.
     *
     * @param gameState The GameState object.
     */
    abstract void buildCountries(GameState gameState);

    /**
     * This method builds the current context.
     *
     * @param gameState The GameState object.
     */
    abstract void buildCurrentContext(GameState gameState);

    /**
     * This method builds the currentPlayerIndex.
     *
     * @param gameState The GameState object.
     */
    abstract void buildCurrentPlayerIndex(GameState gameState);

    /**
     * This method builds the borders.
     *
     * @param gameState The GameState object.
     */
    abstract void buildNumberOfTradedSet(GameState gameState);

    abstract void buildArmiesTradedForSet(GameState gameState);

    abstract void buildCardCount(GameState gameState);
}
