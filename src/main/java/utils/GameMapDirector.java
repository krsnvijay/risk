package utils;

import models.GameMap;
import models.state.GameState;

/**
 * Director for the GameMap
 */
public class GameMapDirector {
    /**
     * builder for the game map
     */
    private GameMapBuilder builder;

    /**
     * setter for the game map builder
     *
     * @param gameMapBuilder builder to be used by the director
     */
    public void setBuilder(GameMapBuilder gameMapBuilder) {
        builder = gameMapBuilder;
    }

    /**
     * construct game map from the game state
     * @param gameState contains game state
     */
    public void constructGameMap(GameState gameState) {
        builder.createGameMap();
        builder.buildPlayersList(gameState);
        builder.buildDeck(gameState);
        builder.buildBorders(gameState);
        builder.buildContinents(gameState);
        builder.buildCountries(gameState);
        builder.buildCurrentContext(gameState);
        builder.buildCurrentPlayerIndex(gameState);
        builder.buildNumberOfTradedSet(gameState);
        builder.buildArmiesTradedForSet(gameState);
        builder.buildCardCount(gameState);
    }

    /**
     * Getter for game map from the builder
     * @return gameMap that is built
     */
    public GameMap getGameMap() {
        return builder.getGameMap();
    }
}
