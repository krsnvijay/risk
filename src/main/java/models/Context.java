package models;

import java.util.Arrays;
import java.util.stream.Stream;

import static models.Command.*;

/**
 * This enum maintains the context the commands are being called in, i.e. if the command is valid
 * for the phase we're currently in.
 */
public enum Context {
  MAIN_MENU(EDIT_MAP, GAME_PLAYER, LOAD_MAP),
  MAP_EDITOR(SHOW_MAP, EDIT_CONTINENT, EDIT_COUNTRY, EDIT_NEIGHBOR, VALIDATE_MAP, SAVE_MAP),
  GAME_SETUP(GAME_HELP, SHOW_MAP, EXIT_GAME, GAME_PLAYER, POPULATE_COUNTRIES),
  GAME_STARTUP(SHOW_MAP_OWNERSHIP, PLACE_ARMY, PLACE_ALL),
  GAME_REINFORCE(SHOW_MAP_OWNERSHIP, REINFORCE),
  GAME_ATTACK(ATTACK),
  GAME_ATTACK_BATTLE_ATTACKER(ATTACK),
  GAME_ATTACK_BATTLE_DEFENDER(DEFEND),
  GAME_ATTACK_BATTLE_VICTORY(ATTACK_MOVE),
  GAME_FORTIFY(SHOW_MAP_OWNERSHIP, FORTIFY);
  Command[] validCommands;
  Command[] defaultCommands = {GAME_HELP, EXIT_GAME};

  Context(Command... validCommands) {
    this.validCommands =
        Stream.of(validCommands, defaultCommands).flatMap(Stream::of).toArray(Command[]::new);
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
