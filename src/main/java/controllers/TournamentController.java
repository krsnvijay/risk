package controllers;

import models.GameMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        String gamePlayerCmd =
            String.format(
                "gameplayer %s",
                Arrays.stream(listOfPlayerStrategies)
                    .map(strategy -> String.format("-add %s %s", strategy.toUpperCase(), strategy))
                    .collect(Collectors.joining(" ")));
        MainController.processGamePlayerCommand(gameMap, gamePlayerCmd);
        // load map
        String loadMapCmd = String.format("loadmap %s", map);
        if (!MainController.processLoadMapCommand(gameMap, loadMapCmd)) {
          continue;
        }
        SetupController.processPopulateCountriesCommand(gameMap, null);
        StartUpController.processPlaceAllCommand(gameMap, null);
        if (GameMap.isGameOver) {
          // save the winners name
          if (!resultTable.containsKey(map)) {
            resultTable.put(map, new ArrayList<String>());
          }
          resultTable.get(map).add(gameMap.getCurrentPlayer().getStrategy().getPlayerName());
        } else {
          // game hasn't completed but number of turns are over
          // save DRAW as the result
          if (!resultTable.containsKey(map)) {
            resultTable.put(map, new ArrayList<String>());
          }
          resultTable.get(map).add("Draw");
        }
        GameMap.destroyGameMap();
      }
    }
    // pretty print the results
    System.out.println(resultTable.toString());
    System.exit(0);
    return true;
  }
}
