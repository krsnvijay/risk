package controllers;

import models.GameMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TournamentController {
  public static int maxNumberOfTurnsProperty;
  private static Map<String, ArrayList<String>> resultTable = new HashMap<>();

  public static boolean startTournament(GameMap gameMap, String command) {
    String[] commandSplitOne = command.split(" -M ");
    String[] commandSplitTwo = commandSplitOne[1].split(" -P ");
    String[] commandSplitThree = commandSplitTwo[1].split(" -G ");

    String[] listOfMapFiles = commandSplitTwo[0].split(" ");
    String[] listOfPlayerStrategies = commandSplitThree[0].split(" ");
    int numberOfGames = Integer.parseInt(commandSplitThree[1].split(" -D ")[0]);
    maxNumberOfTurnsProperty = Integer.parseInt(commandSplitThree[1].split(" -D ")[1]);
    for (String map : listOfMapFiles) {
      for (int gameIndex = 0; gameIndex < numberOfGames; gameIndex++) {
        gameMap = GameMap.getGameMap();
        GameController.isTournament = true;
        // set players
        // Add index to game player names to make them unique
        StringBuilder stringBuilder = new StringBuilder("gameplayer");
        for (int idx = 0; idx < listOfPlayerStrategies.length; idx++) {
          String strategy = listOfPlayerStrategies[idx];
          stringBuilder.append(
                  String.format(" -add %s-%d %s", strategy.toUpperCase(), idx, strategy));
        }
        String gamePlayerCmd = stringBuilder.toString();
        MainController.processGamePlayerCommand(gameMap, gamePlayerCmd);
        // load map
        String loadMapCmd = String.format("loadmap %s", map);
        if (!MainController.processLoadMapCommand(gameMap, loadMapCmd)) {
          continue;
        }
        SetupController.processPopulateCountriesCommand(gameMap, null);
        StartUpController.processPlaceAllCommand(gameMap, null);
        while (!GameMap.isGameOver) {
          GameController.startPhaseLoop(GameMap.getGameMap());
        }
        // save the winners name
        if (!resultTable.containsKey(map)) {
          resultTable.put(map, new ArrayList<String>());
        }
        resultTable.get(map).add(gameMap.getCurrentPlayer().getStrategy().getPlayerName());
        GameMap.destroyGameMap();
      }
    }
    // pretty print the results
    System.out.println(resultTable.toString());
    System.exit(0);
    return true;
  }
}
