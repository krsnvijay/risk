package views;

import java.io.IOException;
import java.util.Arrays;
import models.GameMap;
import utils.CLI;
import utils.EditMap;

public class Runner {

  private static void beginEditor(EditMap editMap, GameMap gameMap) {
    while (true) {
      CLI cli = CLI.getInstance();
      String userInput = CLI.input.nextLine();
      String userCommand = userInput.split(" ")[0];
      String[] opCommands = null;

      if (userInput.startsWith("edit")) {
        String[] originalSplit = userInput.split(" -");
        opCommands = Arrays.copyOfRange(originalSplit, 1, originalSplit.length);
      }

      if (cli.validate(userCommand)) {
        switch (userCommand) {
          case "editcontinent":
            editMap.editContinent(opCommands, gameMap);
            break;
          case "editcountry":
            editMap.editCountry(opCommands, gameMap);
            break;
          case "editneighbor":
            editMap.editNeighbor(opCommands, gameMap);
            break;
          case "validatemap":
            // TODO
            break;
          case "showmap":
            System.out.println(gameMap);
            break;
        }
      } else
        return;
    }
  }

  public static void main(String[] args) throws IOException, Exception {
    CLI cli = CLI.getInstance();
    cli.setCurrentContext(CLI.Context.MAIN_MENU);
    while (true) {
      String userCommand = CLI.input.nextLine();
      if (cli.validate(userCommand)) {
        switch (userCommand.split(" ")[0]) {
          case "editmap":
            cli.setCurrentContext(CLI.Context.EDITOR);
            EditMap editMap = new EditMap();
            String fileName = userCommand.split(" ")[1];
            GameMap gameMap = editMap.loadMap(fileName);
            System.out.println(gameMap);
            beginEditor(editMap, gameMap);
            break;
          case "loadmap":
            cli.setCurrentContext(CLI.Context.GAME_SETUP);
            break;
        }
      } else {
        System.out.println("Invalid command.");
      }
    }
  }
}
