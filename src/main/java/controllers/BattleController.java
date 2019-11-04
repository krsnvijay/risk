package controllers;

import models.*;
import utils.CLI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.reverseOrder;
import static java.util.stream.Collectors.toCollection;
import static views.ConsoleView.display;

public class BattleController {

  private int numOfDiceAttacker;
  private Country attackingCountry;
  private Country defendingCountry;
  private String attackerName;
  private String defenderName;
  private GameMap gameMap;
  private int numOfDiceDefender;
  private boolean isAllOutEnabled = false;

  public BattleController(GameMap gameMap, String command) {
    this.gameMap = gameMap;
    String[] commandSplit = command.split(" ");
    attackingCountry = gameMap.getCountries().get(commandSplit[1]);
    attackerName = gameMap.getCurrentPlayer().getPlayerName();
    defendingCountry = gameMap.getCountries().get(commandSplit[2]);
    if (!command.contains("-allout"))
      numOfDiceAttacker = Integer.parseInt(commandSplit[3]);
    defenderName = defendingCountry.getOwnerName();
    isAllOutEnabled = command.contains("-allout");
  }

  public int calculateMaxDiceForAttacker() {
    int armies = attackingCountry.getNumberOfArmies();
    if (armies > 3) {
      return 3;
    } else if (armies > 1)
      return armies - 1;
    else
      return 0;
  }

  public int calculateMaxDiceForDefender() {
    int armies = defendingCountry.getNumberOfArmies();
    if (armies > 1) {
      return 2;
    } else if (armies == 1) {
      return 1;
    } else {
      return 0;
    }
  }

  public ArrayList<Boolean> compareDiceRolls(
      ArrayList<Integer> attackerDiceRoll, ArrayList<Integer> defenderDiceRoll) {
    Iterator<Integer> attackerDiceIterator =
        attackerDiceRoll.stream().sorted(reverseOrder()).iterator();
    Iterator<Integer> defenderDiceIterator =
        defenderDiceRoll.stream().sorted(reverseOrder()).iterator();
    ArrayList<Boolean> diceComparisonResult = new ArrayList<>();
    while (attackerDiceIterator.hasNext() && defenderDiceIterator.hasNext()) {
      int maxAttacker = attackerDiceIterator.next();
      int maxDefender = defenderDiceIterator.next();
      display(
          String.format("comparing dice %d(attacker) with %d(defender)", maxAttacker, maxDefender),
          true);
      boolean result = maxAttacker > maxDefender;
      display(String.format("%s won", result ? "Attacker" : "Defender"), true);
      diceComparisonResult.add(result);
    }
    return diceComparisonResult;
  }

  /**
   * Rolls multiple dice
   *
   * @param numOfDice number of dice to roll
   * @return result of each dice roll
   */
  public ArrayList<Integer> rollDice(int numOfDice) {
    Supplier<Integer> roll = () -> (int) (Math.random() * 6) + 1;
    return Stream.generate(roll).limit(numOfDice).collect(toCollection(ArrayList::new));
  }

  public boolean startBattle() {
    gameMap.setCurrentContext(Context.GAME_ATTACK_BATTLE_DEFENDER);
    if (isAllOutEnabled) {
      display("Attacker enabled allout, will always choose max dice", true);
      while (gameMap.getCurrentContext() != Context.GAME_ATTACK) {
        performAttack();
      }
    } else {
      performAttack();
      display("Choose another attack move or move to next phase", true);
      gameMap.setCurrentContext(Context.GAME_ATTACK);
    }
    return true;
  }

  public int getNumOfDiceAttacker() {
    if (isAllOutEnabled) {
      return calculateMaxDiceForAttacker();
    } else {
      return numOfDiceAttacker;
    }
  }

  public int getNumOfDiceFromDefender() {
    int defenderNumOfDice = 0;
    if (isAllOutEnabled) {
      return calculateMaxDiceForDefender();
    }
    while (true) {
      display(String.format("%s(defender)'s turn", defenderName), true);
      display(
          String.format(
              "DefendingCountry %s owned by %s has %d armies",
              defendingCountry.getName(), defenderName, defendingCountry.getNumberOfArmies()),
          true);
      display(
          String.format(
              "AttackingCountry %s owned by %s has %d armies",
              attackingCountry.getName(), attackerName, attackingCountry.getNumberOfArmies()),
          true);
      String inputCommand = CLI.input.nextLine().trim();
      Optional<Command> matchedCommand =
          Context.GAME_ATTACK_BATTLE_DEFENDER.getMatchedCommand(inputCommand);
      if (!matchedCommand.isPresent()) {
        display("Invalid command, use help to check the list of available commands", false);
        continue;
      }
      if (matchedCommand.get() == Command.DEFEND) {
        String[] commandSplit = inputCommand.split(" ");
        defenderNumOfDice = Integer.parseInt(commandSplit[1]);
        // Defender can only use 1 or 2 dice
        if (defenderNumOfDice != 1 && defenderNumOfDice != 2) {
          display("Error: Defender can only defend with 1 or 2 Dice", false);
          continue;
        }
        // Need atleast 2 armies to use 2 Dice
        if (defenderNumOfDice == 2 && defendingCountry.getNumberOfArmies() < 2) {
          display("Error: Need atleast 2 armies to use 2 Dice", false);
          continue;
        }
        return defenderNumOfDice;
      } else {
        matchedCommand.ifPresent(command -> command.runOperation(gameMap, inputCommand));
      }
    }
  }

  public void performAttack() {
    numOfDiceDefender = getNumOfDiceFromDefender();
    numOfDiceAttacker = getNumOfDiceAttacker();

    display(String.format("Rolling %d dice for attacker", numOfDiceAttacker), false);
    ArrayList<Integer> attackerDiceRoll = rollDice(numOfDiceAttacker);
    display(String.format("Rolling %d dice for defender", numOfDiceDefender), false);
    ArrayList<Integer> defenderDiceRoll = rollDice(numOfDiceDefender);
    // Compare diceRoll results

    display(
        String.format("%s (attacker) rolled : %s", attackerName, attackerDiceRoll.toString()),
        true);
    display(
        String.format("%s (defender) rolled : %s", defenderName, defenderDiceRoll.toString()),
        true);
    ArrayList<Boolean> results = compareDiceRolls(attackerDiceRoll, defenderDiceRoll);
    for (boolean result : results) {
      if (result) {
        successfulAttack();
      } else {
        successfulDefence();
      }
      if (gameMap.getCurrentContext() == Context.GAME_ATTACK) {
        break;
      }
    }
  }

  public int getNumOfArmiesToMoveFromAttacker() {
    int numOfArmies = 0;
    while (true) {
      display(
          String.format(
              "%s has %d armies, Choose numOfArmies to move to new territory %s",
              attackingCountry.getName(),
              attackingCountry.getNumberOfArmies(),
              defendingCountry.getName()),
          true);
      display(String.format("%s(attacker)'s turn", attackerName), true);
      String inputCommand = CLI.input.nextLine().trim();
      Optional<Command> matchedCommand =
          Context.GAME_ATTACK_BATTLE_VICTORY.getMatchedCommand(inputCommand);
      if (!matchedCommand.isPresent()) {
        display("Invalid command, use help to check the list of available commands", false);
        continue;
      }
      if (matchedCommand.get() == Command.ATTACK_MOVE) {
        String[] commandSplit = inputCommand.split(" ");
        numOfArmies = Integer.parseInt(commandSplit[1]);
        // num of armies should be > 0 and < available armies
        if (numOfArmies < numOfDiceAttacker || numOfArmies >= attackingCountry
            .getNumberOfArmies()) {
          display(
              String.format(
                  "Error: Num of armies to move should be >= %d (num of dice used in attack) and < available armies",
                  numOfDiceAttacker),
              false);
          continue;
        }
        return numOfArmies;
      } else {
        matchedCommand.ifPresent(command -> command.runOperation(gameMap, inputCommand));
      }
    }
  }

  private void successfulBattle() {
    // move armies to new territory
    int numOfArmiesToMove = getNumOfArmiesToMoveFromAttacker();
    attackingCountry.removeArmies(numOfArmiesToMove);
    defendingCountry.setNumberOfArmies(numOfArmiesToMove);
    display(
        String.format(
            "Moved %d armies from %s to %s",
            numOfArmiesToMove, attackingCountry.getName(), defendingCountry.getName()),
        false);
  }

  public void successfulDefence() {
    if (attackingCountry.getNumberOfArmies() > 1) {
      attackingCountry.removeArmies(1);
      display(
          String.format(
              "Defend successful: Removed 1 army from %s (attackingCountry)",
              attackingCountry.getName()),
          true);
      display("Remaining armies in defendingCountry " + defendingCountry.getNumberOfArmies(), true);
    } else if (attackingCountry.getNumberOfArmies() == 1) {
      display(
          String.format(
              "Defend successful: %s(attackingCountry) has only one army and can't attack",
              attackingCountry.getName()),
          true);

      display("Ending battle, choose another attack move", true);
      gameMap.setCurrentContext(Context.GAME_ATTACK);
    }
  }

  public void successfulAttack() {
    if (defendingCountry.getNumberOfArmies() > 1) {
      defendingCountry.removeArmies(1);
      display(
          String.format(
              "Attack successful: Removed 1 army from %s (defendingCountry)",
              defendingCountry.getName()),
          true);
      display("Remaining armies in defendingCountry " + defendingCountry.getNumberOfArmies(), true);

    } else if (defendingCountry.getNumberOfArmies() == 1) {
      display(
          String.format(
              "Attack successful: Attacker Won the battle and conquered the country %s",
              defendingCountry.getName()),
          true);
      defendingCountry.setOwnerName(attackerName);
      display(
          String.format(
              "Changing ownership of %s from %s to %s",
              defendingCountry.getName(), defenderName, attackerName),
          true);
      // Change context to Battle victory
      gameMap.setCurrentContext(Context.GAME_ATTACK_BATTLE_VICTORY);
      // Move armies to the new territory
      successfulBattle();
      boolean isDefenderHavingCountries =
          Player.checkPlayerOwnsAtleastOneCountry(defenderName, gameMap);
      if (!isDefenderHavingCountries) {
        display(String.format("Removing %s from the game due to no ownership", defenderName), true);
        gameMap.removeGamePlayer(defenderName);
      }

      // Check Game Victory condition
      if (Player.checkPlayerOwnsAllTheCountries(attackerName, gameMap)) {
        // Player Won the Game, Exit
        display(String.format("%s(attacker) won the game!", attackerName), true);
        System.exit(0);
      } else {
        // Change to initial attack phase where player can choose to battle again
        display("Ending battle, choose another attack move", true);
        gameMap.setCurrentContext(Context.GAME_ATTACK);
      }
    }
  }
}
