package views;

import static models.Context.*;
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

  // TODO move to a decent place
  public String getPhaseName(Context context) {
    switch(context) {
      case GAME_ATTACK: return "Attack Phase";
      case GAME_FORTIFY: return "Fortify Phase";
      case GAME_REINFORCE: return "Reinforcement Phase";
      case GAME_SETUP: return "Setup Phase";
      default: return "Game not started...";
    }
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    // TODO refactor this entirely (modularize)

    // Declarations
    Label titleLabel, controlLabel, armyLabel;
    Label phaseNameLabel, playerLabel, phaseInfoLabel;
    primaryStage.setTitle("Risk by Group 2");

    // Parent layout
    VBox vbox = new VBox();
    vbox.setFillWidth(true);

    // World Domination View Section
    BorderPane WDSection = new BorderPane();
    WDSection.setStyle("-fx-background-color: #cecece;");
    WDSection.setPadding(new Insets(0, 0, 0, 0));
    WDSection.setMinHeight(300);

    // WD: title label
    titleLabel = new Label("World Domination Information");
    titleLabel.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
    WDSection.setTop(titleLabel);
    BorderPane.setAlignment(titleLabel, Pos.CENTER);
    BorderPane.setMargin(titleLabel, new Insets(10, 0, 0, 0));

    // WD: control % label
    VBox controlSection = new VBox();
    controlLabel = new Label("Controlled territory:");
    // TODO dynamically add labels for players...
    Label player1 = new Label("Siddhant: 30%");
    Label player2 = new Label("Warren: 10%");
    controlSection.getChildren().addAll(controlLabel, player1, player2);
    WDSection.setLeft(controlSection);

    // WD: TODO map here...

    // WD: army counts
    VBox armySection = new VBox();
    armyLabel = new Label("Total armies:");
    // TODO dynamically add labels for players...
    Label army1 = new Label("Siddhant: 34");
    Label army2 = new Label("Warren: 14");
    armySection.getChildren().addAll(armyLabel, army1, army2);
    WDSection.setRight(armySection);

    // Phase View Section
    VBox phaseSection = new VBox();
    phaseSection.setPadding(new Insets(10, 0, 0, 40));

    // PV: components
    phaseNameLabel = new Label("Setup Phase");
    phaseNameLabel.setStyle("-fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 18px");
    phaseNameLabel.setPadding(new Insets(0, 0, 10, 0));

    playerLabel = new Label("Siddhant's Turn");
    playerLabel.setStyle("-fx-font-weight: bold");
    playerLabel.setPadding(new Insets(0, 0, 10, 0));

    phaseInfoLabel = new Label("Siddhant rolled 2 dice and attacked Albania (Owned by Sabari).\n" +
            "Siddhant lost 3 armies.\n" +
            "Sabari lost 2 armies\nAttack Phase ends...\n");

    phaseSection.getChildren().addAll(phaseNameLabel, playerLabel, phaseInfoLabel);

    vbox.getChildren().addAll(WDSection, phaseSection);
    Scene scene = new Scene(vbox, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
