package utils;

import models.GameMap;
import models.state.GameState;

public class GameMapDirector {
    private GameMapBuilder builder;

    public void setBuilder(GameMapBuilder gameMapBuilder) {
        builder = gameMapBuilder;
    }

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

    public GameMap getGameState() {
        return builder.getGameMap();
    }
}
