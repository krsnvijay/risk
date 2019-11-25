package utils;

import models.GameMap;
import models.state.GameState;

public abstract class GameMapBuilder {
    protected GameMap gameMap;

    public GameMap getGameMap() {
        return gameMap;
    }

    public void createGameMap() {
        gameMap = new GameMap();
    }

    abstract void buildPlayersList(GameState gameState);

    abstract void buildDeck(GameState gameState);

    abstract void buildBorders(GameState gameState);

    abstract void buildContinents(GameState gameState);

    abstract void buildCountries(GameState gameState);

    abstract void buildCurrentContext(GameState gameState);

    abstract void buildCurrentPlayerIndex(GameState gameState);

    abstract void buildNumberOfTradedSet(GameState gameState);

    abstract void buildArmiesTradedForSet(GameState gameState);

    abstract void buildCardCount(GameState gameState);
}
