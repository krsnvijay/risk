package views;

import static views.ConsoleView.display;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Context;
import utils.CLI;

/**
 * Runs the project and handles the initial commands.
 *
 * @author Siddhant Bansal
 */
public class Runner extends Application {

  public static void main(String[] args) {
    CLI cli = CLI.getInstance();
    PhaseView phaseView = new PhaseView();
    WDView wdView = new WDView();
    launch(args);
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

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Risk by Group 2");
    VBox vbox = new VBox();
    vbox.setFillWidth(true);
    vbox.setSpacing(200);
    vbox.setAlignment(Pos.CENTER);
    HBox WDSection = new HBox();
    WDSection.setAlignment(Pos.CENTER);
    Label test1, test2;
    test1 = new Label("World Domination Area...");
    test2 = new Label("Phase View Area...");
    WDSection.getChildren().add(test1);
    BorderPane phaseSection = new BorderPane();
    phaseSection.setPadding(new Insets(10, 0, 0, 40));
    phaseSection.setLeft(test2);
    vbox.getChildren().addAll(WDSection, phaseSection);
    Scene scene = new Scene(vbox, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
