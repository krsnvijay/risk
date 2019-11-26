package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controllers.GameController;
import models.GameMap;
import models.state.GameState;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static views.ConsoleView.display;

public class GamePersistenceHandler {
    public static boolean saveState(String fileName) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ConcreteGameStateBuilder concreteGameStateBuilder = new ConcreteGameStateBuilder();
        GameStateDirector gameStateDirector= new GameStateDirector();
        gameStateDirector.setBuilder(concreteGameStateBuilder);
        gameStateDirector.constructGameState(GameMap.getGameMap());
        GameState gameState = gameStateDirector.getGameState();
        String json = gson.toJson(gameState);
        Files.write(Paths.get(fileName),json.getBytes());
        display("Saved game successfully to "+fileName,true);
        return true;
    }

    public static boolean loadState(String fileName) throws FileNotFoundException {
        Gson gson = new Gson();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        GameState gameState = gson.fromJson(bufferedReader, GameState.class);
        ConcreteGameMapBuilder concreteGameMapBuilder = new ConcreteGameMapBuilder();
        GameMapDirector gameMapDirector = new GameMapDirector();
        gameMapDirector.setBuilder(concreteGameMapBuilder);
        gameMapDirector.constructGameMap(gameState);
        GameMap.modifyInstance(gameMapDirector.getGameState());
        display("Loaded game state successfully!",true);
        GameController.startPhaseLoop(GameMap.getGameMap());
        return true;
    }
}