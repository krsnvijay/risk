package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author s_anakih
 *
 */
public class Player {

  private String playername;



  public Player(String playername) {
    super();
    this.playername = playername;

  }

  /**
   * @return playername
   */
  public String getPlayername() {
    return playername;
  }

  public void setPlayername(String playername) {
    this.playername = playername;
  }

  @Override
  public String toString() {
    return String.format("%s", this.playername);
  }

  public static boolean checkPlayerCount(ArrayList<String> playername, int countriesSize) {

    if (playername.size() <= 1) {
      System.out.println("there should be atleast two players to play");
      return true;
    } else if (playername.size() > countriesSize) {
      System.out.println("the player count exceeds the number of countries in the map");
    }
    return false;
  }

  public static void addOrRemovePlayer(ArrayList<String> commands, ArrayList<Player> players) {
    for (String command : commands) {
      String[] commandSplit = command.split(" ");
      Player p = new Player(commandSplit[1]);
      switch (commandSplit[0]) {
        case "add":
          if (!players.contains(p)) {
            players.add(p);
            System.out.println("added" + p.getPlayername());
          } else
            System.out.println(commandSplit[1] + " Player already exists");
          break;
        case "remove":
          if (players.contains(p)) {
            players.remove(p);
            System.out.println("removed" + p.getPlayername());
          } else
            System.out.println(commandSplit[1] + " Player does not exist");
      }
      break;
    }
  }

  public static ArrayList<Country> populateCountries(ArrayList<Player> players, GameMap gameMap) {
    ArrayList<Country> countries =
        gameMap.getCountries().values().stream().collect(Collectors.toCollection(ArrayList::new));
    Collections.shuffle(countries);
    int countrySize = countries.size();
    int playerCount = players.size();
    for (int i = 0; i <= countrySize; i++) {
      countries.get(i).setOwnerName(players.get(i % playerCount).getPlayername());
    }
    return countries;
  }

  public static ArrayList<Country> getCountriesByOwnership(String playername, GameMap gameMap) {
    ArrayList<Country> countries =
        gameMap.getCountries().values().stream().filter(c -> c.getOwnerName().equals(playername))
            .collect(Collectors.toCollection(ArrayList::new));
    return countries;
  }
}

