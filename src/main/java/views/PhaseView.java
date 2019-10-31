package views;

import models.GameMap;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhaseView implements Observer {
  private final ExecutorService service = Executors.newCachedThreadPool();
  private Runner appInstance;

  public PhaseView(Runner app) {
    this.appInstance = app;
  }

  @Override
  public void update(Observable o, Object arg) {
    GameMap gameMap = ((GameMap) o);
    String currentPlayer = gameMap.getCurrentPlayer().getPlayerName();
    service.submit(() -> appInstance.updatePlayerLabel(currentPlayer));

    String currentPhase = gameMap.getCurrentContext().name();
    service.submit(() -> appInstance.updatePhaseLabel(currentPhase));
  }
}
