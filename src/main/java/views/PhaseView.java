package views;

import models.GameMap;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * The Phase View which displays information about the phase, current player, and logs.
 *
 * @author Siddhant
 * @version 1.0
 */
public class PhaseView implements Observer {
  /** Service for thread handling. */
  private final ExecutorService service = Executors.newCachedThreadPool();

  /** Instance of the Runner class. */
  private Runner appInstance;

  private static String prevPhase = "PREV";

  /**
   * Constructor that initializes the application.
   *
   * @param app the Runner object.
   */
  public PhaseView(Runner app) {
    this.appInstance = app;
  }

  /**
   * The overridden update method for the observer.
   *
   * @param o the Observable (Game Map in this scenario)
   * @param changed the change, represented as a constant String.
   */
  @Override
  public void update(Observable o, Object changed) {
    GameMap gameMap = ((GameMap) o);
    String currentPhase =
        gameMap.getCurrentContext().name().contains("ATTACK")
            ? "GAME_ATTACK"
            : gameMap.getCurrentContext().name();
    switch ((String) changed) {
      case "PHASE_LOG":
        String phaseLog = gameMap.getPhaseLog();
        service.submit(() -> appInstance.updatePhaseInfoLabel(phaseLog));
        break;
      case "CURRENT_PLAYER":
        String currentPlayer = gameMap.getCurrentPlayer().getPlayerName();
        service.submit(() -> appInstance.updatePlayerLabel(currentPlayer));
        break;
      case "CURRENT_CONTEXT":
        if (currentPhase.equals("GAME_STARTUP")) {
          gameMap
              .getPlayersList()
              .forEach(
                  player -> {
                    player.addObserver(CardExchangeView.getInstance());
                  });
        }

        if (currentPhase.equals("GAME_REINFORCE")) {
          service.submit(() -> appInstance.updateCardView());
        }

        if (currentPhase.equals("GAME_ATTACK")) {
          service.submit(() -> appInstance.clearCardView());
        }
        // clear phase log
        if(!prevPhase.equals(currentPhase)) {
          gameMap.setPhaseLog("", true);
          prevPhase = currentPhase;
        }
        service.submit(() -> appInstance.updatePhaseLabel(currentPhase));
        break;
    }
  }
}
