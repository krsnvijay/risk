package views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import models.Context;
import models.GameMap;
import models.WorldDomination;
import utils.CLI;

import java.util.*;
import static views.ConsoleView.display;

/**
 * Runs the project and handles the initial commands.
 *
 * @author Siddhant Bansal
 * @version 1.0
 */
public class Runner extends Application {
  /** Contains the list of Observers. */
  public static List<Observer> ObserverList = new ArrayList<>();

  /** Labels for WD View. */
  private Label rootTitleLabel, rootControlLabel, rootArmyLabel;

  /** Labels for the Phase View. */
  private Label rootPhaseNameLabel, rootPlayerLabel, rootPhaseInfoLabel;

  /** Label for the continent section. */
  private Label rootContinentLabel;

  /** BorderPane hosting the WD view. */
  private BorderPane WDSection;

  /** HBox hosting the Card View. */
  private HBox cardSection = new HBox();

  /** ScrollPane for scrollable cards in hand. */
  private ScrollPane cardScroller = new ScrollPane();

  /** An Array of Labels for cards **/
  private ArrayList<Label> cardLabels = new ArrayList<>();

  /** A Temporary Map of Players mapped to cards they have in hand **/
  private Map<String, List<String>> cardLabelStringsTempMap = new HashMap<>();

  /** Constructor for the runner class. */
  public Runner() {
    rootPlayerLabel = new Label("Game Not Started!");
    rootPhaseNameLabel = new Label("...");
    rootPhaseInfoLabel = new Label("Game has not started yet!");
    rootTitleLabel = new Label("World Domination Information");
    rootControlLabel = new Label("Controlled territory:");
    rootControlLabel.setStyle("-fx-font-weight: bold;");
    rootArmyLabel = new Label("Total armies:");
    rootArmyLabel.setPadding(new Insets(15, 150, 0, 0));
    rootArmyLabel.setStyle("-fx-font-weight: bold;");
    for (int i = 0; i < 5; i++) {
      Label card = new Label("");
      card.setPadding(new Insets(10, 10, 10, 10));
      card.setStyle("-fx-border-color: black; -fx-border-radius: 2px; -fx-border-insets: 5");
      cardLabels.add(card);
    }
  }

  /**
   * Launches the GUI and the program.
   *
   * @param args command line arguments.
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Updates the player label on the GUI.
   *
   * @param labelValue the name of the player.
   */
  public void updatePlayerLabel(String labelValue) {
    Platform.runLater(
        () -> {
          rootPlayerLabel.setText(labelValue);
        });
  }

  /**
   * Updates the Phase Info label.
   *
   * @param labelValue the new string to be logged.
   */
  public void updatePhaseInfoLabel(String labelValue) {
    Platform.runLater(
        () -> {
          rootPhaseInfoLabel.setText(labelValue);
        });
  }

  /**
   * Updates the phase label.
   *
   * @param labelValue the new string for the phase.
   */
  public void updatePhaseLabel(String labelValue) {
    Platform.runLater(
        () -> {
          rootPhaseNameLabel.setText(labelValue);
        });
  }

  /**
   * Updates the army label.
   *
   * @param labelValue the new values for the armies.
   */
  public void updateArmyLabel(String labelValue) {
    Platform.runLater(
        () -> {
          rootArmyLabel.setText("Total Armies by Player:\n" + labelValue);
        });
  }

  /**
   * Updates the control label.
   *
   * @param labelValue the new values for the percentage controlled.
   */
  public void updateControlLabel(String labelValue) {
    Platform.runLater(
        () -> {
          rootControlLabel.setText("%ge Control by Player:\n" + labelValue);
        });
  }

  /**
   * Updates the continent control label.
   *
   * @param labelValue the new values of the continents controlled.
   */
  public void updateContinentControlLabel(String labelValue) {
    Platform.runLater(
        () -> {
          rootContinentLabel.setText("Continents Controlled By Player:\n" + labelValue);
        });
  }

  /**
   * Update the whole card view
   */
  public void updateCardView() {
    String currPlayer = GameMap.getGameMap().getCurrentPlayer().getPlayerName();
    if (cardLabelStringsTempMap.containsKey(currPlayer)) {
      List<String> cardInHandStrings = cardLabelStringsTempMap.get(currPlayer);
      for (int i = 0; i < cardLabels.size(); i++) {
        if (i < cardInHandStrings.size()) {
          int finalI = i;
          Platform.runLater(
              () -> {
                cardLabels.get(finalI).setText(cardInHandStrings.get(finalI));
                cardLabels.get(finalI).setVisible(true);
              });
        } else {
          int finalI1 = i;
          Platform.runLater(
              () -> {
                cardLabels.get(finalI1).setVisible(false);
                cardLabels.get(finalI1).setText("");
              });
        }
      }
    }
  }

  /**
   * Update the termporary map of cards in hand for each player
   * @param cardsInHandStrings
   */
  public void updateCardLabelsTemp(List<String> cardsInHandStrings) {
    String currPlayer = GameMap.getGameMap().getCurrentPlayer().getPlayerName();
    cardLabelStringsTempMap.put(currPlayer, cardsInHandStrings);
  }

  /** Initiates and processes the command line for the whole game. */
  public static void processCommandline() {
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

  /**
   * Returns the name of a phase.
   *
   * @param context the Context enum to get the name of the phase.
   * @return the String with a user-friendly name.
   */
  public static String getPhaseName(Context context) {
    switch (context) {
      case GAME_ATTACK:
        return "Attack Phase";
      case GAME_FORTIFY:
        return "Fortify Phase";
      case GAME_REINFORCE:
        return "Reinforcement Phase";
      case GAME_SETUP:
        return "Setup Phase";
      default:
        return "Game not started...";
    }
  }

  /**
   * Adds the card view to the WD View.
   *
   * @param WDSection The BorderPane hosting the WD View.
   */
  private void addCardView(BorderPane WDSection) {
    cardScroller.setContent(cardSection);
    cardScroller.setStyle("-fx-background-color: #b0b0b0;");
    cardScroller.addEventFilter(
        ScrollEvent.SCROLL,
        new EventHandler<ScrollEvent>() {
          @Override
          public void handle(ScrollEvent event) {
            if (event.getDeltaY() != 0) {
              event.consume();
            }
          }
        });
    cardSection.setPadding(new Insets(10, 0, 10, 0));
    cardSection.setAlignment(Pos.CENTER);
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

    cardLabels.stream()
        .forEach(
            cardLabel -> {
              if (cardLabel.getText().equals("")) cardLabel.setVisible(false);
              addCardToView(cardLabel);
            });

    cardSection.setStyle("-fx-background-color: #b0b0b0;");
    cardScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    HBox.setHgrow(cardScroller, Priority.ALWAYS);

    WDSection.setBottom(cardScroller);
  }

  /**
   * Adds the continent control section to the WD View.
   *
   * @param WDSection the BorderPane hosting the WD View.
   */
  private void addMapSection(BorderPane WDSection) {
    VBox mapSection = new VBox();
    mapSection.setStyle("-fx-background-color: #cecece");
    mapSection.setAlignment(Pos.TOP_CENTER);
    mapSection.setPadding(new Insets(25, 0, 0, -10));
    rootContinentLabel = new Label("Continents Controlled By Players");
    mapSection.getChildren().add(rootContinentLabel);
    WDSection.setCenter(mapSection);
  }

  /**
   * Adds the total armies section to the WD View.
   *
   * @param WDSection the BorderPane hosting the WD View.
   */
  private void addArmySection(BorderPane WDSection) {
    VBox armySection = new VBox();
    armySection.setAlignment(Pos.TOP_CENTER);
    armySection.setPadding(new Insets(10, 0, 0, 0));
    armySection.getChildren().addAll(rootArmyLabel);
    WDSection.setRight(armySection);
  }

  /**
   * Populates the WD View.
   *
   * @param WDSection the BorderPane hosting the WD View.
   */
  private void populateWDView(BorderPane WDSection) {
    WDSection.setStyle("-fx-background-color: #cecece;");
    WDSection.setPadding(new Insets(0, 0, 0, 0));
    WDSection.setMinHeight(300);

    // Sets the title at the top.
    rootTitleLabel.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
    WDSection.setTop(rootTitleLabel);
    WDSection.setLeft(rootControlLabel);
    rootControlLabel.setPadding(new Insets(25, 0, 0, 125));
    BorderPane.setAlignment(rootTitleLabel, Pos.CENTER);
    BorderPane.setMargin(rootTitleLabel, new Insets(25, 0, 0, 0));
    BorderPane.setAlignment(rootControlLabel, Pos.TOP_RIGHT);
    addMapSection(WDSection);
    addArmySection(WDSection);
  }

  /**
   * Populates the Phase View.
   *
   * @param phaseSection the VBox hosting the phase view.
   */
  private void populatePhaseView(VBox phaseSection) {
    phaseSection.setPadding(new Insets(25, 0, 0, 40));

    rootPhaseNameLabel.setStyle(
        "-fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 18px");
    rootPhaseNameLabel.setPadding(new Insets(0, 0, 10, 0));

    rootPlayerLabel.setStyle("-fx-font-weight: bold");
    rootPlayerLabel.setPadding(new Insets(0, 0, 10, 0));

    ScrollPane phaseScroller = new ScrollPane();
    phaseScroller.setFitToWidth(true);
    phaseScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    phaseScroller.setContent(rootPhaseInfoLabel);
    phaseSection.getChildren().addAll(rootPhaseNameLabel, rootPlayerLabel, phaseScroller);
  }

  /**
   * Adds a card to the view.
   *
   * @param name the complete name of the card.
   */
  private void addCardToView(Label card) {
    cardSection.getChildren().add(card);
  }

  /** Removes the card view from the screen. */
  public void clearCardView() {
    cardLabels.forEach(cardLabel -> cardLabel.setVisible(false));
  }

  /**
   * Starts the GUI.
   *
   * @param primaryStage The main Stage for the UI.
   */
  @Override
  public void start(Stage primaryStage) {
    try {
      GameMap gameMap = GameMap.getGameMap();
      CLI cli = CLI.getInstance();
      PhaseView phaseView = new PhaseView(this);
      WDView wdView = new WDView(this);

      gameMap.addObserver(phaseView);
      WorldDomination.getInstance().addObserver(wdView);
      ObserverList.add(phaseView);

      CardExchangeView _cardExchangeView = new CardExchangeView(this);

      primaryStage.setTitle("Risk by Group 2");
      VBox vbox = new VBox();
      vbox.setFillWidth(true);

      WDSection = new BorderPane();
      populateWDView(WDSection);

      addCardView(WDSection);

      VBox phaseSection = new VBox();
      populatePhaseView(phaseSection);

      vbox.getChildren().addAll(WDSection, phaseSection);
      Scene scene = new Scene(vbox, 800, 600);
      primaryStage.setScene(scene);
      primaryStage.show();

      Thread cliThread = new Thread(Runner::processCommandline);
      cliThread.setDaemon(true);
      cliThread.start();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
