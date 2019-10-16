package views;

import static views.ConsoleView.display;

import models.Context;
import utils.CLI;

/**
 * Runs the project and handles the initial commands.
 *
 * @author Siddhant Bansal
 */
public class Runner {

  public static void main(String[] args) {
    CLI cli = CLI.getInstance();
    cli.setCurrentContext(Context.MAIN_MENU);
    display("Welcome to risk game");
    display("Type help to see available commands");
    while (true) {
      String command = CLI.input.nextLine();
      boolean commandStatus = cli.getCurrentContext().runCommand(CLI.getGameMap(), command.trim());
      if (!commandStatus) {
        display("Invalid command, use help to check the list of available commands");
      }
    }
  }
}
