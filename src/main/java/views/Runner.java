package views;

import controllers.GameRunner;
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
    CLI cli = CLI.getInstance();
    while (true) {
      System.out.println(
          "available commands \n editcontinent -add <continentname> <continentvalue> -remove <continentname> \n editcountry -add <countryname> <continentname> -remove <countryname> \n editneighbor -add <countryname> <neighborcountryname> -remove <countryname> <neighborcountryname> \n validatemap \n showmap \n exiteditor");
      String userInput = CLI.input.nextLine();
      String userCommand = userInput.split(" ")[0];
      String[] opCommands = null;
      if (userInput.startsWith("edit")) {
        String[] originalSplit = userInput.split(" -");
        if (originalSplit.length > 1)
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
            opCommands = userInput.split(" ");
            if (editMap.validateMap(gameMap)) {
              try {
                MapParser.saveMap(gameMap, opCommands[1]);
              } catch (Exception e) {
                System.out.println(e.getMessage());
              }
            } else System.out.println("Invalid Map!");
            break;
          case "validatemap":
            if (editMap.validateMap(gameMap)) System.out.println("Valid Map!");
            else System.out.println("Invalid map!");
            break;
          case "showmap":
            System.out.println(gameMap);
            break;
          case "exiteditor": {
            System.out.println("EXITED Map Editor! You have abandoned any changes made.");
          }
            return;
        }
      } else {
        System.out.println(
            "You entered an invalid command. The valid commands are: \n"
                + "editcontinent, editcountry, editneighbor, savemap, validatemap, showmap, and exiteditor");
      }
    }
  }

  /**
   * The main method which runs the whole project. It parses user commands and initiates the game or
   * editor.
   *
   * @param args The command line arguments as a String array.
   */
  public static void main(String[] args) {
    CLI cli = CLI.getInstance();
    cli.setCurrentContext(CLI.Context.MAIN_MENU);
    System.out.println("Welcome to The Game of Risk! :)");
    while (true) {
      System.out.println(
          "available commands \n editmap <fileName> \n loadmap <fileName> \n gameplayer -add <playerName> -remove <PlayerName>");
      String userCommand = CLI.input.nextLine();
      if (cli.validate(userCommand)) {
        switch (userCommand.split(" ")[0]) {
          case "editmap":
            try {
              String fileName = userCommand.split(" ")[1];
              GameMap gameMap = EditMap.loadMap(fileName);
              cli.setCurrentContext(CLI.Context.EDITOR);
              System.out.println(gameMap);
              beginEditor(new EditMap(), gameMap);
            } catch (Exception e) {
              System.out.println(e.getMessage());
            }
            break;
          case "loadmap":
            try {
              GameMap loadedMap = MapParser.loadMap(userCommand.split(" ")[1]);
              GameRunner gameRunner = new GameRunner(loadedMap);
              System.out.println("Map has been LOADED!");
              gameRunner.gameSetup();
            } catch (Exception e) {
              System.out.println(e.getMessage());
            }
            break;
          case "gameplayer":
            String[] commandSplit = userCommand.split(" -");
            String[] optionsArray = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
            GameRunner.gamePlayer(new ArrayList<String>(Arrays.asList(optionsArray)));
            break;
        }
      }
    }
  }
}
