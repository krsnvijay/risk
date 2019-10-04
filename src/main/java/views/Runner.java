package views;

import java.io.IOException;
import utils.CLI;
import utils.MapParser;

public class Runner {
  public static void main(String[] args) throws IOException, Exception {
    CLI cli = CLI.getInstance();
    cli.setCurrentContext(CLI.Context.MAIN_MENU);
    while (true) {
      String userCommand = CLI.input.nextLine();
      if (cli.validate(userCommand)) {
        switch (userCommand) {
          case "editmap":
            cli.setCurrentContext(CLI.Context.EDITOR);
            String fileName = userCommand.split(" ")[1];
            MapParser.loadMap(fileName);
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
