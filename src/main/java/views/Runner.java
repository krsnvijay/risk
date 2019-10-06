package views;

import java.io.IOException;
import java.util.Arrays;
import models.GameMap;
import utils.CLI;
import utils.EditMap;

/**
 * Runs the project and handles the initial commands.
 * 
 * @author Siddhant Bansal
 *
 */
public class Runner {

  /**
   * The CLI Map Editor. Handles map editing commands for addition and removal of continents,
   * countries, and neighbours. Also handles validation and displaying the map.
   * 
   * @param editMap An instance of EditMap.
   * @param gameMap An instance of the whole GameMap.
   * @throws Exception when there's an invalid command.
   */
  private static void beginEditor(EditMap editMap, GameMap gameMap) throws Exception {
    while (true) {
      CLI cli = CLI.getInstance();
      String userInput = CLI.input.nextLine();
      String userCommand = userInput.split(" ")[0];
      String[] opCommands = null;

      if (userInput.startsWith("save")) {
        opCommands = userInput.split(" ");
        if (opCommands.length != 2) {
          throw new Exception("Invalid Command");
        }
      } else if (userInput.startsWith("edit")) {
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
          case "savemap":
            if (editMap.validateMap(gameMap))
              editMap.saveMap(gameMap, opCommands[1]);
            else
              throw new Exception("Invalid map!");
            break;
          case "validatemap":
            if (editMap.validateMap(gameMap))
              System.out.println("Valid Map!");
            else
              throw new Exception("Invalid map!");
            break;
          case "showmap":
            System.out.println(gameMap);
            break;
        }
      } else
        return;
    }
  }

  /**
   * The main method which runs the whole project. It parses user commands and initiates the game or
   * editor.
   * 
   * @param args The command line arguments as a String array.
   * @throws IOException TODO: will handle the exception.
   * @throws Exception TODO: will handle the exception.
   */
  public static void main(String[] args) throws IOException, Exception {
    CLI cli = CLI.getInstance();
    cli.setCurrentContext(CLI.Context.MAIN_MENU);
    while (true) {
      try {
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
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }
}
