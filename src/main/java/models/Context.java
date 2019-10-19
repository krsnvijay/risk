package models;

import static models.Command.ATTACK_NONE;
import static models.Command.EDIT_CONTINENT;
import static models.Command.EDIT_COUNTRY;
import static models.Command.EDIT_MAP;
import static models.Command.EDIT_NEIGHBOR;
import static models.Command.EXIT_GAME;
import static models.Command.FORTIFY;
import static models.Command.FORTIFY_NONE;
import static models.Command.GAME_HELP;
import static models.Command.GAME_PLAYER;
import static models.Command.LOAD_MAP;
import static models.Command.PLACE_ALL;
import static models.Command.PLACE_ARMY;
import static models.Command.POPULATE_COUNTRIES;
import static models.Command.REINFORCE;
import static models.Command.SAVE_MAP;
import static models.Command.SHOW_MAP;
import static models.Command.SHOW_MAP_OWNERSHIP;
import static models.Command.VALIDATE_MAP;

import java.util.Arrays;

/**
 * This enum maintains the context the commands are being called in, i.e. if the command is valid
 * for the phase we're currently in.
 */
public enum Context {
  MAIN_MENU(GAME_HELP, EXIT_GAME, EDIT_MAP, GAME_PLAYER, LOAD_MAP),
  MAP_EDITOR(
      GAME_HELP,
      EXIT_GAME,
      SHOW_MAP,
      EDIT_CONTINENT,
      EDIT_COUNTRY,
      EDIT_NEIGHBOR,
      VALIDATE_MAP,
      SAVE_MAP),
  GAME_SETUP(GAME_HELP, SHOW_MAP, EXIT_GAME, GAME_PLAYER, POPULATE_COUNTRIES),
  GAME_STARTUP(GAME_HELP, EXIT_GAME, SHOW_MAP_OWNERSHIP, PLACE_ARMY, PLACE_ALL),
  GAME_REINFORCE(GAME_HELP, EXIT_GAME, SHOW_MAP_OWNERSHIP, REINFORCE),
  GAME_ATTACK(GAME_HELP, EXIT_GAME, ATTACK_NONE),
  GAME_FORTIFY(GAME_HELP, EXIT_GAME, SHOW_MAP_OWNERSHIP, FORTIFY, FORTIFY_NONE);
  Command[] validCommands;

  Context(Command... validCommands) {
    this.validCommands = validCommands;
  }

  public boolean validate(String riskCommand) {
    return Arrays.stream(validCommands).map(Command::getRegex).anyMatch(riskCommand::matches);
  }

  public boolean runCommand(GameMap gameMap, String riskCommand) {
    for (Command commandValidator : validCommands) {
      if (commandValidator.validate(riskCommand)) {
        return commandValidator.runOperation(gameMap, riskCommand);
      }
    }
    return false;
  }

  public Command[] getValidCommands() {
    return validCommands;
  }
}
