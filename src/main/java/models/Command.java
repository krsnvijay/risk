package models;

import controllers.EditorController;
import controllers.GameController;
import controllers.MainController;
import controllers.SetupController;
import controllers.StartUpController;
import java.util.function.BiPredicate;

/**
 * Enum to store command regex pattern, mapping function and usage Can be used command validation
 * and calling relevant controller function
 */
public enum Command {
  GAME_HELP("^help$", MainController::gameHelp, "help"),
  EXIT_GAME("^exitgame$", MainController::exitGame, "exitgame"),
  EDIT_MAP("^editmap (.+)$", MainController::editMap, "editmap <fileLocation>"),
  EDIT_CONTINENT(
      "^editcontinent( -(add (\\w+) (\\d+)|remove (\\w+)))+$",
      EditorController::editContinent,
      "editcontinent -add <continentName> <controlValue> -remove <continentName>"),
  EDIT_COUNTRY(
      "^editcountry( -(add (\\w+) (\\w+)|remove (\\w+)))+$",
      EditorController::editCountry,
      "editcountry -add <countryName> <continentName> -remove <countryName>"),
  EDIT_NEIGHBOR(
      "^editneighbor( -(add|remove) (\\w+) (\\w+))+$",
      EditorController::editNeighbor,
      "editneighbor -add <countryName1> <countryName2> -remove <countryName1> <countryName2>"),
  VALIDATE_MAP("^validatemap$", EditorController::validateMap, "validatemap"),
  SAVE_MAP("^savemap (.+)$", EditorController::saveMap, "savemap <fileLocation>"),
  GAME_PLAYER(
      "^gameplayer( -(add|remove) (\\w+))+$",
      MainController::gamePlayer,
      "gameplayer -add <playerName> -remove <playerName>"),
  LOAD_MAP("^loadmap (.+)$", MainController::loadMap, "loadmap <fileLocation>"),
  POPULATE_COUNTRIES(
      "^populatecountries$", SetupController::populateCountries, "populatecountries"),
  PLACE_ARMY("^placearmy (\\w+)$", StartUpController::placeArmy, "placearmy <countryName>"),
  PLACE_ALL("^placeall$", StartUpController::placeAll, "placeall"),
  REINFORCE(
      "^reinforce (\\w+) (\\d+)$",
      GameController::reinforce,
      "reinforce <countryName> <armyCount>"),
  FORTIFY(
      "^fortify (\\w+) (\\w+) (\\d+)$",
      GameController::fortify,
      "fortify <fromCountryName> <toCountryName> <armyCount>"),
  FORTIFY_NONE("^fortify none$", GameController::fortifyNone, "fortify none"),
  ATTACK_NONE("^attack none$", GameController::attackNone, "attack none"),
  SHOW_MAP("^showmap$", EditorController::showMap, "showmap"),
  SHOW_MAP_OWNERSHIP("^showmap$", GameController::showMap, "showmap");
  String regex;
  BiPredicate<GameMap, String> operation;
  String usage;

  /**
   * Enum constructor
   *
   * @param regex command pattern matching
   * @param operation operation to run on the command
   * @param usage info about a command's usage
   */
  Command(String regex, BiPredicate<GameMap, String> operation, String usage) {
    this.regex = regex;
    this.operation = operation;
    this.usage = usage;
  }

  /**
   * Getter for regex
   *
   * @return regex pattern
   */
  public String getRegex() {
    return regex;
  }

  /**
   * getter for operation
   *
   * @return operation
   */
  public BiPredicate<GameMap, String> getOperation() {
    return operation;
  }

  /**
   * Validates a command with a patter
   *
   * @param riskCommand command to validate
   * @return boolean to indicate status
   */
  public boolean validate(String riskCommand) {
    return riskCommand.matches(regex);
  }

  /**
   * Getter for usage of a command
   *
   * @return string about the usage of the risk command
   */
  public String getUsage() {
    return usage;
  }

  /**
   * Runs an operation on a gamestate and a command
   *
   * @param gameMap contains game state
   * @param riskCommand command from the cli
   * @return boolean to indicate status
   */
  public boolean runOperation(GameMap gameMap, String riskCommand) {
    if (validate(riskCommand)) {
      return operation.test(gameMap, riskCommand);
    }
    return false;
  }
}
