package views;

import static models.GameMap.getGameMap;

/**
 * View for cli
 *
 * @author Vijay
 * @version 1.0
 */
public class ConsoleView {

  /**
   * Displays text to the console
   *
   * @param writeLog whether log should be written down
   * @param text string to display
   */
  public static void display(String text, boolean writeLog) {
    if (writeLog) {
      getGameMap().setPhaseLog(String.format("-> %s\n", text), false);
    }
    System.out.println("-> " + text);
  }
}
