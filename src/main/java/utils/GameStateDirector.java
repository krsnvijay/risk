package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.GameMap;
import models.state.GameState;

import java.io.*;

public class GameStateDirector {
  private GameStateBuilder builder;

  public void setBuilder(GameStateBuilder gameStateBuilder) {
    builder = gameStateBuilder;
  }

  public void constructGameState(GameMap gameMap) {
    builder.createGameState();
    builder.buildPlayersList(gameMap);
    builder.buildDeck(gameMap);
    builder.buildBorders(gameMap);
    builder.buildContinents(gameMap);
    builder.buildCountries(gameMap);
    builder.buildCurrentContext(gameMap);
    builder.buildCurrentPlayerIndex(gameMap);
    builder.buildNumberOfTradedSet(gameMap);
    builder.buildArmiesTradedForSet(gameMap);
    builder.buildCardCount(gameMap);
  }

  public GameState getGameState() {
    return builder.getGameState();
  }


}
