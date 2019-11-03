package views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
    rootPhaseNameLabel = new Label("...");
    rootPhaseInfoLabel = new Label("Game has not started yet!");
    rootTitleLabel = new Label("World Domination Information");
    rootControlLabel = new Label("Controlled territory:");
    rootControlLabel.setStyle("-fx-font-weight: bold;");
    rootArmyLabel = new Label("Total armies:");
    rootArmyLabel.setStyle("-fx-font-weight: bold;");
  }

  public static void main(String[] args) {
    launch(args);
  }

  public void updatePlayerLabel(String labelValue) {
    Platform.runLater(() -> {
      rootPlayerLabel.setText(labelValue);
    });
  }

  public void updatePhaseInfoLabel(String labelValue) {
    Platform.runLater(() -> {
      rootPhaseInfoLabel.setText(labelValue);
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
    display("Welcome to risk game", false);
    display("Type help to see available commands", false);
    while (true) {
      String command = CLI.input.nextLine();
      boolean commandStatus = gameMap.getCurrentContext().runCommand(gameMap, command.trim());
      if (!commandStatus) {
        display("Invalid command, use help to check the list of available commands", false);
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

  private void addCardView(BorderPane WDSection) {
    HBox cardSection = new HBox();
    cardSection.setPadding(new Insets(10, 0, 10, 0));
    cardSection.setAlignment(Pos.CENTER);
    cardSection.setStyle("-fx-background-color: grey;");
    Label sampleCard2 = new Label("Toronto Cavalry");
    sampleCard2.setPadding(new Insets(10, 10, 10, 10));
    sampleCard2.setStyle("-fx-border-color: black; -fx-border-radius: 2px; -fx-border-insets: 5");
    Label sampleCard3 = new Label("Vancouver Infantry");
    sampleCard3.setPadding(new Insets(10, 10, 10, 10));
    sampleCard3.setStyle("-fx-border-color: black; -fx-border-radius: 2px; -fx-border-insets: 5");
    cardSection.getChildren().addAll(sampleCard2, sampleCard3);
    WDSection.setBottom(cardSection);
  }

  private void addControlSection(BorderPane WDSection) {
    VBox controlSection = new VBox();
    controlSection.setAlignment(Pos.CENTER_LEFT);
    controlSection.setPadding(new Insets(0, 0, 0, 25));
    // TODO dynamically add labels for players...
    Label player1 = new Label("Siddhant: 30%");
    Label player2 = new Label("Warren: 10%");
    controlSection.getChildren().addAll(rootControlLabel, player1, player2);
    WDSection.setLeft(controlSection);
  }

  private void addMapSection(BorderPane WDSection) {
    HBox placeholder = new HBox();
    placeholder.setStyle("-fx-background-color: darkgrey");
    WDSection.setCenter(placeholder);
  }

  private void addArmySection(BorderPane WDSection) {
    VBox armySection = new VBox();
    armySection.setAlignment(Pos.CENTER_RIGHT);
    armySection.setPadding(new Insets(0, 25, 0, 0));
    // TODO dynamically add labels for players...
    Label army1 = new Label("Siddhant: 34");
    Label army2 = new Label("Warren: 14");
    armySection.getChildren().addAll(rootArmyLabel, army1, army2);
    WDSection.setRight(armySection);
  }

  private void populateWDView(BorderPane WDSection) {
    WDSection.setStyle("-fx-background-color: #cecece;");
    WDSection.setPadding(new Insets(0, 0, 0, 0));
    WDSection.setMinHeight(300);

    // Sets the title at the top.
    rootTitleLabel.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
    WDSection.setTop(rootTitleLabel);
    BorderPane.setAlignment(rootTitleLabel, Pos.CENTER);
    BorderPane.setMargin(rootTitleLabel, new Insets(25, 0, 0, 0));

    addControlSection(WDSection);
    addMapSection(WDSection);
    addArmySection(WDSection);
  }

  private void populatePhaseView(VBox phaseSection) {
    phaseSection.setPadding(new Insets(25, 0, 0, 40));

    rootPhaseNameLabel.setStyle("-fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 18px");
    rootPhaseNameLabel.setPadding(new Insets(0, 0, 10, 0));

    rootPlayerLabel.setStyle("-fx-font-weight: bold");
    rootPlayerLabel.setPadding(new Insets(0, 0, 10, 0));

    phaseSection.getChildren().addAll(rootPhaseNameLabel, rootPlayerLabel, rootPhaseInfoLabel);
  }

  private VBox spawnCardView(String name) {
    Label sampleCard = new Label(name);
    sampleCard.setPadding(new Insets(10, 10, 10, 10));
    sampleCard.setStyle("-fx-border-color: black; -fx-border-radius: 2px; -fx-border-insets: 5");

    return new VBox();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    GameMap gameMap = GameMap.getGameMap();
    CLI cli = CLI.getInstance();
    PhaseView phaseView = new PhaseView(this);
    WDView wdView = new WDView(this);

    gameMap.addObserver(phaseView);
    gameMap.addObserver(wdView);
    ObserverList.add(phaseView);
    ObserverList.add(wdView);

    primaryStage.setTitle("Risk by Group 2");
    VBox vbox = new VBox();
    vbox.setFillWidth(true);

    BorderPane WDSection = new BorderPane();
    populateWDView(WDSection);
    // TODO call conditionally
    addCardView(WDSection);
    // TODO to "clear" the view, use WDSection.setBottom(null)

    VBox phaseSection = new VBox();
    populatePhaseView(phaseSection);

    vbox.getChildren().addAll(WDSection, phaseSection);
    Scene scene = new Scene(vbox, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();

    Thread cliThread = new Thread(Runner::processCommandline);
    cliThread.setDaemon(true);
    cliThread.start();
  }
}
