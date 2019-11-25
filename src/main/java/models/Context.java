package models;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static models.Command.*;

/**
 * This enum maintains the context the commands are being called in, i.e. if the command is valid
 * for the phase we're currently in.
 *
 * @author Vijay
 * @version 1.0
 */
public enum Context {
  MAIN_MENU(EDIT_MAP, GAME_PLAYER, LOAD_MAP, START_TOURNAMENT),
  MAP_EDITOR(SHOW_MAP, EDIT_CONTINENT, EDIT_COUNTRY, EDIT_NEIGHBOR, VALIDATE_MAP, SAVE_MAP),
  GAME_SETUP(GAME_HELP, SHOW_MAP, EXIT_GAME, GAME_PLAYER, POPULATE_COUNTRIES),
  GAME_STARTUP(SHOW_MAP_OWNERSHIP, PLACE_ARMY, PLACE_ALL),
  GAME_REINFORCE(SAVE_GAME, SHOW_MAP_OWNERSHIP, REINFORCE, EXCHANGE_CARDS),
  GAME_ATTACK(SAVE_GAME, SHOW_MAP_OWNERSHIP, ATTACK),
  GAME_ATTACK_BATTLE_DEFENDER(DEFEND),
  GAME_ATTACK_BATTLE_VICTORY(ATTACK_MOVE),
  GAME_FORTIFY(SAVE_GAME, SHOW_MAP_OWNERSHIP, FORTIFY);
  Command[] validCommands;
  Command[] defaultCommands = {GAME_HELP, EXIT_GAME};

  /**
   * The constructor for the Context enum.
   *
   * @param validCommands All the valid commands as Command objects.
   */
  Context(Command... validCommands) {
    this.validCommands =
        Stream.of(validCommands, defaultCommands).flatMap(Stream::of).toArray(Command[]::new);
  }

  /**
   * Validates a command.
   *
   * @param riskCommand The command string.
   * @return boolean result of the validation.
   */
  public boolean validate(String riskCommand) {
    return Arrays.stream(validCommands).map(Command::getRegex).anyMatch(riskCommand::matches);
  }

  /**
   * Returns the matching Command object from the string provided.
   *
   * @param riskCommand the command String.
   * @return the Command object.
   */
  public Optional<Command> getMatchedCommand(String riskCommand) {
    return Arrays.stream(validCommands).filter(c -> c.validate(riskCommand)).findFirst();
  }

  /**
   * Executes the command's behaviour.
   *
   * @param gameMap The Game Map instance.
   * @param riskCommand the command entered by the user.
   * @return boolean result of execution.
   */
  public boolean runCommand(GameMap gameMap, String riskCommand) {
    for (Command commandValidator : validCommands) {
      if (commandValidator.validate(riskCommand)) {
        return commandValidator.runOperation(gameMap, riskCommand);
      }
    }
    return false;
  }

  /**
   * Returns a list of valid commands.
   *
   * @return Command object array with valid commands.
   */
  public Command[] getValidCommands() {
    return validCommands;
  }
}
