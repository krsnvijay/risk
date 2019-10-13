package controllers;

import models.Country;
import models.GameMap;
import models.Player;
import utils.CLI;
import utils.CLI.Context;

import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Controls the Game Loop.
 *
 * @author Siddhant Bansal
 */
public class GameRunner {

  private static ArrayList<Player> playersList = new ArrayList<>();
  private static int currentPlayerIndex = 0;
  private GameMap gameMap;
  private CLI cli = CLI.getInstance();
  private boolean isGameStarted = false;

  /**
   * @param gameMap GameMap object that stores map data i.e borders, countries, files, continents
   */
  public GameRunner(GameMap gameMap) {
    this.gameMap = gameMap;
    cli.setCurrentContext(Context.GAME_SETUP);
  }

  /**
   * Validates the count of players in the game.
   *
   * @param playerNames a list of names of every player
   * @param countriesSize the size of the countries list.
   * @return A boolean with success or failure.
   */
  public static boolean validatePlayerCount(ArrayList<String> playerNames, int countriesSize) {
    if (playerNames.size() <= 1) {
      System.out.println("There should be at least two players to play.");
      return true;
    } else if (playerNames.size() > countriesSize) {
      System.out.println("The player count exceeds the number of countries in the map.");
    }
    return false;
  }

  public static void updatePlayerIndex() {
    currentPlayerIndex = (currentPlayerIndex + 1) % playersList.size();
  }

  public static Player getCurrentPlayer() {
    return playersList.get(currentPlayerIndex);
  }

  /**
   * This processes the addplayer command.
   *
   * @param commands an arraylist of options
   * @author sabari
   */
  public static void gamePlayer(ArrayList<String> commands) {
    for (String command : commands) {
      String[] commandSplit = command.split(" ");
      Player p = new Player(commandSplit[1]);
      switch (commandSplit[0]) {
        case "add":
          if (!playersList.contains(p)) {
            playersList.add(p);
            System.out.println("Added " + p.getPlayerName());
          } else {
            System.out.println(commandSplit[1] + " Player already exists");
          }
          break;
        case "remove":
          if (playersList.contains(p)) {
            playersList.remove(p);
            System.out.println("Removed" + p.getPlayerName());
          } else {
            System.out.println(commandSplit[1] + " Player does not exist");
          }
          break;
      }
    }
  }

  /** */
  public void gameSetup() {
    int[] totalArmyCounts = {40, 35, 30, 25, 20};
    int armiesPerPlayer;
    int placedArmies = 0;
    while (true) {
      String userCommand = CLI.input.nextLine();
      if (cli.validate(userCommand)) {
        switch (userCommand.split(" ")[0]) {
          case "populatecountries":
            armiesPerPlayer = totalArmyCounts[playersList.size() - 2];
            for (Player p : playersList) {
              p.setNumberOfArmies(armiesPerPlayer);
            }
            isGameStarted = true;
            gameMap.setCountries(populateCountries());
            break;
          case "placearmy":
            if (!isGameStarted) {
              System.out.println("Start the game first -- populatecountries.");
            } else {
              if (placeArmy(userCommand.split(" ")[1])) {
                System.out.println(
                    "Player "
                        + playersList.get(currentPlayerIndex).getPlayerName()
                        + " placed the army.");
                if (checkGameReady()) {
                  gameLoop();
                  return;
                } else updatePlayerIndex();
              } else {
                System.out.println("Unable to place army!");
              }
            }
            break;
          case "placeall":
            if (!isGameStarted) System.out.println("Start the game first -- populatecountries.");
            else {
              for (int turn = 0; turn < playersList.size(); turn++) {
                ArrayList<Country> countriesForPlayer =
                    Player.getCountriesByOwnership(getCurrentPlayer().getPlayerName(), gameMap);
                Random randomGen = new Random();
                while (getCurrentPlayer().getNumberOfArmies() > 0) {
                  int randomIndexCountry = randomGen.nextInt(countriesForPlayer.size());
                  placeArmy(countriesForPlayer.get(randomIndexCountry).getName());
                }
                if (turn != playersList.size() - 1) updatePlayerIndex();
              }
              gameLoop();
              return;
            }
            break;
          case "gameplayer":
            if (isGameStarted) {
              System.out.println("Can't add players once the game has started!");
            } else {
              String[] commandSplit = userCommand.split(" -");
              String[] optionsArray = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
              gamePlayer(new ArrayList<>(Arrays.asList(optionsArray)));
              break;
            }
            break;
          default:
            System.out.println("Invalid Commands");
        }
      }
    }
  }

  /**
   * Checks if the setup is complete and the game is ready to begin
   *
   * @return true if numberOfArmies for each player is 0 ; else returns false
   */
  private boolean checkGameReady() {
    return playersList.stream().mapToInt(Player::getNumberOfArmies).allMatch(count -> count == 0);
  }

  /**
   * This method places an army in a country that the player owns
   *
   * @param countryName name of the country to place an army
   * @return A boolean with success or failure.
   */
  private boolean placeArmy(String countryName) {
    Player currentPlayer = playersList.get(currentPlayerIndex);
    String currentPlayerName = currentPlayer.getPlayerName();
    Country currentCountry = gameMap.getCountries().get(countryName);
    if (currentCountry.getOwnerName().equals(currentPlayerName)
        && currentPlayer.getNumberOfArmies() > 0) {
      currentCountry.addArmies(1);
      currentPlayer.subtractArmies(1);
      return true;
    }
    return false;
  }

  /**
   * This method places an army in a country that the player owns
   *
   * @param countryName name of the country to place an army
   * @param numArmies   armies to place
   * @return A boolean with success or failure.
   */
  private boolean placeArmy(String countryName, int numArmies) {
    Player currentPlayer = playersList.get(currentPlayerIndex);
    String currentPlayerName = currentPlayer.getPlayerName();
    Country currentCountry = gameMap.getCountries().get(countryName);
    if (currentCountry.getOwnerName().equals(currentPlayerName)
        && currentPlayer.getNumberOfArmies() > 0) {
      currentCountry.addArmies(numArmies);
      currentPlayer.subtractArmies(numArmies);
      return true;
    }
    return false;
  }

  /** */
  public void gameLoop() {

    while (true) {
      Player currentPlayer = getCurrentPlayer();
      System.out.println(gameMap.showMapByOwnership());
      System.out.println(currentPlayer.getPlayerName() + "'s turn:");
      CLI cli = CLI.getInstance();
      for (Phases phase : Phases.values()) {
        switch (phase) {
          case REINFORCE:
            cli.setCurrentContext(Context.GAME_REINFORCE);
            System.out.println("[Reinforce Phase]");
            currentPlayer.setNumberOfArmies(currentPlayer.calculateReinforcements(gameMap));
            while (currentPlayer.getNumberOfArmies() > 0) {
              System.out.println(currentPlayer.getPlayerName() + " has " + currentPlayer.getNumberOfArmies() + " army(s) to reinforce");
              String userCommand = CLI.input.nextLine();
              if (userCommand.trim().equals("showmap"))
                System.out.println(gameMap.showMapByOwnership());
              else {
                String[] opCmds = userCommand.split(" ");
                if (opCmds.length != 3) {
                  System.out.println("Invalid command: Usage reinforce <countryName> <armyCount>");
                  continue;
                }
                String countryToPlace = opCmds[1];
                int armiesToPlace = Integer.parseInt(opCmds[2]);
                placeArmy(countryToPlace, armiesToPlace);
                System.out.println("Reinforced " + countryToPlace + " with " + armiesToPlace + " armies");
              }


            }
            break;
          case ATTACK:
            cli.setCurrentContext(Context.GAME_ATTACK);
            System.out.println("[Attack Phase]");
            System.out.println("You can't attack, Yet");
            break;
          case FORTIFY:
            cli.setCurrentContext(Context.GAME_FORTIFY);
            System.out.println("[Fortify Phase]");
            break;

        }

      }
      updatePlayerIndex();
    }
//    while (true) {
//      // run turns here...
//    }
  }

  enum Phases {
    REINFORCE,
    ATTACK,
    FORTIFY
  }

  /**
   * This method randomly populates all the countries on the map.
   *
   * @return a map of countries with ownership
   * @author sabari
   */
  public Map<String, Country> populateCountries() {
    ArrayList<Country> countries = new ArrayList<>(gameMap.getCountries().values());
    Collections.shuffle(countries);
    int countrySize = countries.size();
    int playerCount = playersList.size();
    for (int i = 0; i < countrySize; i++) {
      countries.get(i).setOwnerName(playersList.get(i % playerCount).getPlayerName());
    }
    return countries.stream().collect(toMap(Country::getName, c -> c));
  }
}
