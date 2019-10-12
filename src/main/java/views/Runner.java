package views;

import controllers.GameRunner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import models.GameMap;
import utils.CLI;
import utils.EditMap;
import utils.MapParser;

/**
 * Runs the project and handles the initial commands.
 *
 * @author Siddhant Bansal
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
            if (editMap.validateMap(gameMap)) editMap.saveMap(gameMap, opCommands[1]);
            else throw new Exception("Invalid map!");
            break;
          case "validatemap":
            if (editMap.validateMap(gameMap)) System.out.println("Valid Map!");
            else throw new Exception("Invalid map!");
            break;
          case "showmap":
            System.out.println(gameMap);
            break;
          case "exiteditor":
            return;
        }
      } else return;
    }
  }

  /** @param gameMap stores map data i.e borders, countries, files, continents */
  private static void beginGame(GameMap gameMap) {
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
            break;
          case "editcountry":
            break;
          case "editneighbor":
            break;
          case "validatemap":
            break;
          case "showmap":
            System.out.println(gameMap);
            break;
        }
      } else return;
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
              String fileName = userCommand.split(" ")[1];
              GameMap gameMap = EditMap.loadMap(fileName);
              System.out.println(gameMap);
              beginEditor(new EditMap(), gameMap);
              break;
            case "loadmap":
              GameMap loadedMap = MapParser.loadMap(userCommand.split(" ")[1]);
              GameRunner gameRunner = new GameRunner(loadedMap);
              gameRunner.gameSetup();
              break;
            case "gameplayer":
              String[] commandSplit = userCommand.split(" -");
              String[] optionsArray = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
              GameRunner.gamePlayer((ArrayList<String>) Arrays.asList(optionsArray));
              break;
          }
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }
}
