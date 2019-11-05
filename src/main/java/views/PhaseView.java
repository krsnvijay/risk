package views;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import models.GameMap;

public class PhaseView implements Observer {
  private final ExecutorService service = Executors.newCachedThreadPool();
  private Runner appInstance;

  public PhaseView(Runner app) {
    this.appInstance = app;
  }

  @Override
  public void update(Observable o, Object changed) {
    GameMap gameMap = ((GameMap) o);
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
        String currentPhase = gameMap.getCurrentContext().name();
        // clear phase log
        gameMap.setPhaseLog("", true);
        service.submit(() -> appInstance.updatePhaseLabel(currentPhase));
        break;
    }
  }
}
