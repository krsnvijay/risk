package views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Context;
import models.GameMap;
import utils.CLI;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import static views.ConsoleView.display;
/**
 * Runs the project and handles the initial commands.
 *
 * @author Siddhant Bansal
 */
public class Runner extends Application {
  public static List<Observer> ObserverList = new ArrayList<>();

  Label rootTitleLabel, rootControlLabel, rootArmyLabel;
  Label rootPhaseNameLabel, rootPlayerLabel, rootPhaseInfoLabel;

  public Runner() {
    rootPlayerLabel = new Label("Game Not Started!");
    rootPhaseNameLabel = new Label("Setup Phase");
    rootPhaseInfoLabel = new Label("Siddhant rolled 2 dice and attacked Albania (Owned by Sabari).\n" +
        "Siddhant lost 3 armies.\n" +
        "Sabari lost 2 armies\nAttack Phase ends...\n");

    rootTitleLabel = new Label("World Domination Information");
    rootControlLabel = new Label("Controlled territory:");
    rootArmyLabel = new Label("Total armies:");
  }

  public static void main(String[] args) {
    launch(args);
  }

  public void updatePlayerLabel(String labelValue) {
    Platform.runLater(() -> {
      rootPlayerLabel.setText(labelValue);
    });
  }

  public void updatePhaseLabel(String labelValue) {
    Platform.runLater(() -> {
      rootPhaseNameLabel.setText(labelValue);
    });
  }

  public static void processCommandline() {
    CLI cli = CLI.getInstance();
    GameMap gameMap = GameMap.getGameMap();
    gameMap.setCurrentContext(Context.MAIN_MENU);
    display("Welcome to risk game");
    display("Type help to see available commands");
    while (true) {
      String command = CLI.input.nextLine();
      boolean commandStatus = gameMap.getCurrentContext().runCommand(gameMap, command.trim());
      if (!commandStatus) {
        display("Invalid command, use help to check the list of available commands");
      }
    }
  }

  // TODO move to a decent place
  public static String getPhaseName(Context context) {
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
    GameMap gameMap = CLI.getGameMap();

    CLI cli = CLI.getInstance();

    PhaseView phaseView = new PhaseView(this);
    WDView wdView = new WDView(this);

    gameMap.addObserver(phaseView);
    gameMap.addObserver(wdView);

    ObserverList.add(phaseView);
    ObserverList.add(wdView);

    // TODO refactor this entirely (modularize)

    // Declarations
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
    rootTitleLabel.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
    WDSection.setTop(rootTitleLabel);
    BorderPane.setAlignment(rootTitleLabel, Pos.CENTER);
    BorderPane.setMargin(rootTitleLabel, new Insets(10, 0, 0, 0));

    // WD: control % label
    VBox controlSection = new VBox();
    // TODO dynamically add labels for players...
    Label player1 = new Label("Siddhant: 30%");
    Label player2 = new Label("Warren: 10%");
    controlSection.getChildren().addAll(rootControlLabel, player1, player2);
    WDSection.setLeft(controlSection);

    // WD: TODO map here...

    // WD: army counts
    VBox armySection = new VBox();
    // TODO dynamically add labels for players...
    Label army1 = new Label("Siddhant: 34");
    Label army2 = new Label("Warren: 14");
    armySection.getChildren().addAll(rootArmyLabel, army1, army2);
    WDSection.setRight(armySection);

    // Phase View Section
    VBox phaseSection = new VBox();
    phaseSection.setPadding(new Insets(10, 0, 0, 40));

    // PV: components
    rootPhaseNameLabel.setStyle("-fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 18px");
    rootPhaseNameLabel.setPadding(new Insets(0, 0, 10, 0));

    rootPlayerLabel.setStyle("-fx-font-weight: bold");
    rootPlayerLabel.setPadding(new Insets(0, 0, 10, 0));

    phaseSection.getChildren().addAll(rootPhaseNameLabel, rootPlayerLabel, rootPhaseInfoLabel);

    vbox.getChildren().addAll(WDSection, phaseSection);
    Scene scene = new Scene(vbox, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();

    Thread cliThread = new Thread(Runner::processCommandline);
    cliThread.setDaemon(true);
    cliThread.start();
  }
}
