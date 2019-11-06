package views;

import models.GameMap;
import models.WorldDomination;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * The class for the World Domination view.
 *
 * @author Siddhant
 * @version 1.0
 */
public class WDView implements Observer {
  /** Thread execution service. */
  private final ExecutorService service = Executors.newCachedThreadPool();

  /** Instance of the Runner class. */
  private Runner appInstance;

  /**
   * Constructor for the WorldDomination view.
   *
   * @param app instance of the Runner class.
   */
  public WDView(Runner app) {
    this.appInstance = app;
  }

  /**
   * The overridden update method for the observer.
   *
   * @param o the Observable (WorldDomination in this scenario)
   * @param arg the arg object is unused in this scenario.
   */
  @Override
  public void update(Observable o, Object arg) {
    WorldDomination worldDomination = (WorldDomination) o;
    List<String> excludedContexts = Arrays.asList("MAIN_MENU", "MAP_EDITOR", "GAME_SETUP");

    if (!excludedContexts.contains(GameMap.getGameMap().getCurrentContext().name())) {
      StringBuilder armyCountLabelString = new StringBuilder();
      for (Map.Entry<String, Integer> entry :
          worldDomination.getTotalArmiesOwnedByPlayer().entrySet()) {
        String key = entry.getKey();
        Integer val = entry.getValue();
        armyCountLabelString.append(String.format("%s -- %d\n", key, val));
      }
      service.submit(() -> appInstance.updateArmyLabel(armyCountLabelString.toString()));

      StringBuilder playerOwnershipLabelString = new StringBuilder();
      for (Map.Entry<String, Double> entry :
          worldDomination.getPlayerOwnerShipPercentage().entrySet()) {
        String key = entry.getKey();
        Double val = entry.getValue();
        playerOwnershipLabelString.append(String.format("%s -- %.2f%%\n", key, val));
      }
      service.submit(() -> appInstance.updateControlLabel(playerOwnershipLabelString.toString()));

      StringBuilder playerContinentOwnershipLabelString = new StringBuilder();
      for (Map.Entry<String, ArrayList<String>> entry :
          worldDomination.getContinentsOwnedByPlayers().entrySet()) {
        String key = entry.getKey();
        ArrayList<String> val = entry.getValue();
        String joinedVal = val.stream().collect(Collectors.joining("\n  "));
        playerContinentOwnershipLabelString.append(
            String.format("%s -- %s\n", key, joinedVal.isEmpty() ? "NO CONTINENT" : joinedVal));
      }
      service.submit(
          () ->
              appInstance.updateContinentControlLabel(
                  playerContinentOwnershipLabelString.toString()));
    }
  }
}
