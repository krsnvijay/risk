package controllers;

import models.Country;
import models.GameMap;
import models.Player;
import utils.CLI;
import utils.CLI.Context;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * Controls the Game Loop. Handles other actions.
 *
 * @author Siddhant Bansal
 */
public class GameRunner {

  /** This maintains a list of players currently in the game. */
  public static ArrayList<Player> playersList = new ArrayList<>();

  /** Maintains whose turn it is (index). */
  private static int currentPlayerIndex = 0;

  /** Keeps a copy of the GameMap to modify and refer. */
  private GameMap gameMap;

  /** Instance of CLI for handling commands. */
  private CLI cli = CLI.getInstance();

  /** Checks whether the game has started (for command processing) */
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
   * @return A boolean with success or failure.
   */
  public static boolean validatePlayerCount(ArrayList<String> playerNames) {
    if (playerNames.size() <= 1) {
      System.out.println(
          "There should be at least two players to play. Please add more players before loading map & starting game.");
      return false;
    }
    if (playerNames.size() > 6) {
      System.out.println(
          "Too many players! Limit is 6 players! Please remove players before loading map & starting game");
      return false;
    }
    return true;
  }

  /** Updates the current player index (round robin fashion) */
  public static void updatePlayerIndex() {
    currentPlayerIndex = (currentPlayerIndex + 1) % playersList.size();
  }

  /**
   * A getter for the current player object.
   *
   * @return Player object, for the player whose turn it is.
   */
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

  /**
   * Game Setup -- handles the Setup phase. All the Setup phase commands and army distribution logic
   * is here.
   */
  public void gameSetup() {
    int[] totalArmyCounts = {40, 35, 30, 25, 20};
    int armiesPerPlayer;
    int placedArmies = 0;
    while (true) {
      System.out.println("[Startup Phase]");
      if (isGameStarted) {
        System.out.println(
            "Available commands \n showmap \n placearmy <countryname> \n placeall \n IF using \"placearmy\" it is "
                + getCurrentPlayer().getPlayerName()
                + "'s turn");
      } else {
        System.out.println(
            "Available commands \n showmap \n gameplayer -add <playername> -remove <playername> \n populatecountries");
      }
      String userCommand = CLI.input.nextLine();
      if (cli.validate(userCommand)) {
        switch (userCommand.split(" ")[0]) {
          case "showmap":
            if (isGameStarted) System.out.println(gameMap.showMapByOwnership());
            else System.out.println(gameMap);
            break;
          case "populatecountries":
            ArrayList<String> playerNames =
                playersList.stream()
                    .map(Player::getPlayerName)
                    .collect(Collectors.toCollection(ArrayList::new));
            if (validatePlayerCount(playerNames)) {
              armiesPerPlayer = totalArmyCounts[playersList.size() - 2];
              for (Player p : playersList) {
                p.setNumberOfArmies(armiesPerPlayer);
              }
              isGameStarted = true;
              gameMap.setCountries(populateCountries(playersList));
              System.out.println("Players have been assigned countries!");
            }
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
              System.out.println("Placed armies on players' countries!");
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
    if (gameMap.getCountries().containsKey(countryName)) {
      Country currentCountry = gameMap.getCountries().get(countryName);
      if (currentCountry.getOwnerName().equals(currentPlayerName)
          && currentPlayer.getNumberOfArmies() > 0) {
        currentCountry.addArmies(1);
        currentPlayer.subtractArmies(1);
        return true;
      }
    }
    return false;
  }

  /**
   * This method places an army in a country that the player owns
   *
   * @param countryName name of the country to place an army
   * @param numArmies armies to place
   * @return A boolean with success or failure.
   */
  private boolean placeArmy(String countryName, int numArmies) {
    Player currentPlayer = playersList.get(currentPlayerIndex);
    String currentPlayerName = currentPlayer.getPlayerName();
    if (gameMap.getCountries().containsKey(countryName)) {
      Country currentCountry = gameMap.getCountries().get(countryName);
      if (currentCountry.getOwnerName().equals(currentPlayerName)
          && currentPlayer.getNumberOfArmies() > 0) {
        currentCountry.addArmies(numArmies);
        currentPlayer.subtractArmies(numArmies);
        return true;
      }
    }
    return false;
  }

  /**
   * The Game Loop method: handles all the logic for processing turns. Reading and executing
   * commands for REINFORCE, ATTACK (TODO), and FORTIFY phases.
   */
  public void gameLoop() {

    while (true) {
      Player currentPlayer = getCurrentPlayer();
      System.out.println(currentPlayer.getPlayerName() + "'s turn:");
      CLI cli = CLI.getInstance();
      for (Phases phase : Phases.values()) {
        switch (phase) {
          case REINFORCE:
            cli.setCurrentContext(Context.GAME_REINFORCE);
            System.out.println("Available commands \n reinforce <countryname> <num> \n showmap");
            System.out.println("[Reinforce Phase]");
            currentPlayer.setNumberOfArmies(currentPlayer.calculateReinforcements(gameMap));

            while (currentPlayer.getNumberOfArmies() > 0) {
              System.out.println(
                  currentPlayer.getPlayerName()
                      + " has "
                      + currentPlayer.getNumberOfArmies()
                      + " army(s) to reinforce");
              String userCommand = CLI.input.nextLine();
              if (userCommand.trim().equals("showmap"))
                System.out.println(
                    gameMap.showMapByOwnershipByCurrentPlayer(currentPlayer.getPlayerName()));
              else {
                String[] opCmds = userCommand.split(" ");
                if (opCmds.length != 3) {
                  System.out.println("Invalid command: Usage reinforce <countryName> <armyCount>");
                  continue;
                }
                String countryToPlace = opCmds[1];
                if (Player.getCountriesByOwnership(currentPlayer.getPlayerName(), gameMap).stream()
                    .noneMatch(c -> c.getName().equals(countryToPlace))) {
                  System.out.println(
                      "Error player doesnt own the country or it does not exist in map");
                  continue;
                }
                int armiesToPlace = Integer.parseInt(opCmds[2]);
                placeArmy(countryToPlace, armiesToPlace);
                System.out.println(
                    "Reinforced " + countryToPlace + " with " + armiesToPlace + " armies");
              }
            }
            break;
          case ATTACK:
            cli.setCurrentContext(Context.GAME_ATTACK);
            System.out.println("[Attack Phase]");
            System.out.println("NOT YET IMPLEMENTED IN GAME");
            break;
          case FORTIFY:
            cli.setCurrentContext(Context.GAME_FORTIFY);
            System.out.println("[Fortification Phase]");
            System.out.println(
                "Available commands \n fortify <fromcountry> <tocountry> <num> \n fortify none \n showmap");
            while (true) {
              System.out.println("[Fortify Phase]");
              String userCommand = CLI.input.nextLine();
              if (userCommand.trim().equals("showmap"))
                System.out.println(
                    gameMap.showMapByOwnershipByCurrentPlayer(currentPlayer.getPlayerName()));
              else {
                String[] opCmds = userCommand.split(" ");
                if (opCmds.length == 2) {
                  if (opCmds[0].equals("fortify") && opCmds[1].equals("none")) {
                    System.out.println(currentPlayer.getPlayerName() + " chose not to fortify!");
                    break;
                  }
                } else if (opCmds.length == 4) {
                  if (opCmds[0].equals("fortify")) {
                    String fromCountry = opCmds[1];
                    String toCountry = opCmds[2];
                    int armyToMove = Integer.parseInt(opCmds[3]);
                    boolean isOwnershipValid =
                        Player.getCountriesByOwnership(currentPlayer.getPlayerName(), gameMap)
                                .stream()
                                .anyMatch(c -> c.getName().equals(fromCountry))
                            && Player.getCountriesByOwnership(
                                    currentPlayer.getPlayerName(), gameMap)
                                .stream()
                                .anyMatch(c -> c.getName().equals(toCountry));
                    if (isOwnershipValid) {
                      boolean isAdjacent =
                          gameMap.getBorders().get(fromCountry).contains(toCountry);
                      if (isAdjacent) {
                        boolean isArmyRemoved =
                            gameMap.getCountries().get(fromCountry).removeArmies(armyToMove);
                        if (isArmyRemoved && armyToMove > 0) {
                          gameMap.getCountries().get(toCountry).addArmies(armyToMove);
                          System.out.println(
                              getCurrentPlayer().getPlayerName()
                                  + " transferred "
                                  + armyToMove
                                  + " army(s) from "
                                  + fromCountry
                                  + " to "
                                  + toCountry);
                          break;
                        } else {
                          System.out.println("Error number of army(s) is not valid");
                        }
                      } else {
                        System.out.println("Error fromCountry and toCountry are not adjacent");
                      }
                    } else {
                      System.out.println(
                          "Error player doesnt own the country or it does not exist in map");
                    }
                  }
                } else {
                  System.out.println(
                      "Invalid command: Usage fortify <fromCountry> <toCountry> <numOfArmies> OR fortify none");
                  continue;
                }
              }
            } // while loop ends
            break;
        }
      }
      if (!Player.checkPlayerOwnsAtleastOneCountry(currentPlayer.getPlayerName(), gameMap)) {
        System.out.println(currentPlayer.getPlayerName() + " owns no countries and was REMOVED");
        playersList.remove(currentPlayer);
      }
      if (playersList.size() == 1) {
        System.out.println(playersList.get(0) + " HAS WON THE GAME!");
        System.exit(0);
      }
      updatePlayerIndex();
    }
  }

  /**
   * This method randomly populates all the countries on the map.
   *
   * @param playerList list of players in the game
   * @return a map of countries with ownership
   * @author sabari
   */
  public Map<String, Country> populateCountries(ArrayList<Player> playerList) {
    ArrayList<Country> countries = new ArrayList<>(gameMap.getCountries().values());
    Collections.shuffle(countries);
    int countrySize = countries.size();
    int playerCount = playerList.size();
    for (int i = 0; i < countrySize; i++) {
      countries.get(i).setOwnerName(playerList.get(i % playerCount).getPlayerName());
    }
    return countries.stream().collect(toMap(Country::getName, c -> c));
  }

  /** This is an enum that maintains the current phase in the turn. */
  enum Phases {
    REINFORCE,
    ATTACK,
    FORTIFY
  }
}
