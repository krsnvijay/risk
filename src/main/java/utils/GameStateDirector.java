package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.GameMap;
import models.GameState;

import java.io.*;

public class GameStateDirector {
  private GameStateBuilder builder;

  public void setBuilder(GameStateBuilder gameStateBuilder) {
    builder = gameStateBuilder;
  }

  public void constructGameState() {
    builder.buildPlayers();
    builder.buildCurrentPlayer();
    builder.buildContinents();
    builder.buildCountries();
    builder.buildBorders();
    builder.buildDeck();
  }

  public GameState getGameState() {
    return builder.getGameState();
  }

  public boolean saveState(String fileName) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    gson.toJson(getGameState(), new FileWriter(fileName));
    return true;
  }

  public boolean loadState(String fileName) throws FileNotFoundException {
    Gson gson = new Gson();
    BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
    GameMap gameMap = gson.fromJson(bufferedReader, GameMap.class);
    GameMap.modifyInstance(gameMap);
    return true;
  }
}
