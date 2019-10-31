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

  public BattleController(GameMap gameMap, String command) {
    this.gameMap = gameMap;
    String[] commandSplit = command.split(" ");
    attackingCountry = gameMap.getCountries().get(commandSplit[1]);
    attackerName = gameMap.getCurrentPlayer().getPlayerName();
    defendingCountry = gameMap.getCountries().get(commandSplit[2]);
    numOfDiceAttacker = Integer.parseInt(commandSplit[3]);
    defenderName = defendingCountry.getOwnerName();
  }

  public int calculateMaxDice(Country attackingCountry) {
    int armies = attackingCountry.getNumberOfArmies();
    if (armies > 5) return 5;
    else if (armies > 1) return armies - 1;
    else return 0;
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
      diceComparisonResult.add(maxAttacker > maxDefender);
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

  public boolean startBattle(boolean isAllOutEnabled) {
    gameMap.setCurrentContext(Context.GAME_ATTACK_BATTLE_DEFENDER);
    while (true) {
      display(String.format("%s(defender)'s turn", defenderName));
      display(
          String.format(
              "DefendingCountry %s owned by %s has %d armies",
              defendingCountry.getName(), defenderName, defendingCountry.getNumberOfArmies()));
      display(
          String.format(
              "AttackingCountry %s owned by %s has %d armies",
              attackingCountry.getName(), attackerName, attackingCountry.getNumberOfArmies()));
      String inputCommand = CLI.input.nextLine().trim();
      Optional<Command> matchedCommand =
          gameMap.getCurrentContext().getMatchedCommand(inputCommand);
      if (!matchedCommand.isPresent()) {
        display("Invalid command, use help to check the list of available commands");
        continue;
      }
      if (matchedCommand.get().equals(Command.DEFEND)) {
        String[] commandSplit = inputCommand.split(" ");
        numOfDiceDefender = Integer.parseInt(commandSplit[1]);
        // Defender can only use 1 or 2 dice
        if (numOfDiceDefender != 1 && numOfDiceDefender != 2) {
          display("Error: Defender can only defend with 1 or 2 Dice");
          return false;
        }
        // Need atleast 2 armies to use 2 Dice
        if (numOfDiceDefender == 2 && defendingCountry.getNumberOfArmies() < 2) {
          display("Error: Need atleast 2 armies to use 2 Dice");
          return false;
        }
        // Always choose max dice for attacker if allout mode is enabled
        if (isAllOutEnabled) {
          display("Attacker enabled allout, will always choose max dice");
          numOfDiceAttacker = calculateMaxDice(attackingCountry);
        }
        display(String.format("Rolling %d dice for attacker", numOfDiceAttacker));
        ArrayList<Integer> attackerDiceRoll = rollDice(numOfDiceAttacker);
        display(String.format("Rolling %d dice for defender", numOfDiceDefender));
        ArrayList<Integer> defenderDiceRoll = rollDice(numOfDiceDefender);
        // Compare diceRoll results

        display(
            String.format("%s (attacker) rolled : %s", attackerName, attackerDiceRoll.toString()));
        display(
            String.format("%s (defender) rolled : %s", defenderName, defenderDiceRoll.toString()));
        ArrayList<Boolean> results = compareDiceRolls(attackerDiceRoll, defenderDiceRoll);
        for (boolean result : results) {
          if (result) {
            if (defendingCountry.getNumberOfArmies() > 1) {
              defendingCountry.removeArmies(1);
              display(
                  String.format(
                      "Attack successful: Removed 1 army from %s (defendingCountry)",
                      defendingCountry.getName()));
              display(
                  "Remaining armies in defendingCountry " + defendingCountry.getNumberOfArmies());

            } else if (defendingCountry.getNumberOfArmies() == 1) {
              defendingCountry.removeArmies(1);
              display(
                  String.format(
                      "Attack successful: Removed 1 army from %s (defendingCountry)",
                      defendingCountry.getName()));
              display(
                  String.format(
                      " Attacker Won the battle and conquered the country %s",
                      defendingCountry.getName()));
              display(
                  "Remaining armies in defendingCountry " + defendingCountry.getNumberOfArmies());
              defendingCountry.setOwnerName(attackerName);
              display(
                  String.format(
                      "Changing ownership of %s from %s to %s",
                      defendingCountry.getName(), defenderName, attackerName));
              boolean isDefenderHavingCountries =
                  Player.checkPlayerOwnsAtleastOneCountry(defenderName, gameMap);
              if (!isDefenderHavingCountries) {
                display(
                    String.format("Removing %s from the game due to no ownership", defenderName));
                gameMap.removeGamePlayer(defenderName);
              }
              // Change context to Battle victory
              gameMap.setCurrentContext(Context.GAME_ATTACK_BATTLE_VICTORY);
              // TODO Move armies to the new territory

              // Check Game Victory condition
              if (!Player.checkPlayerOwnsAtleastOneCountry(attackerName, gameMap)) {
                // Player Won the Game, Exit
                display(String.format("%s(attacker) won the game!", attackerName));
                System.exit(0);
              } else {
                // Change to initial attack phase where player can choose to battle again
                display("Ending battle, choose another attack move");
                gameMap.setCurrentContext(Context.GAME_ATTACK);
                return true;
              }
            }
          } else {
            if (attackingCountry.getNumberOfArmies() > 1) {
              attackingCountry.removeArmies(1);
              display(
                  String.format(
                      "Defend successful: Removed 1 army from %s (attackingCountry)",
                      attackingCountry.getName()));
              display(
                  "Remaining armies in defendingCountry " + defendingCountry.getNumberOfArmies());
              if (!isAllOutEnabled) {
                display("%s(Attacker)'s turn: can attack or abandon");
                gameMap.setCurrentContext(Context.GAME_ATTACK);
                return true;
              }
            } else if (attackingCountry.getNumberOfArmies() == 1) {
              // Remove last army from attacking country
              attackingCountry.removeArmies(1);
              display(
                  String.format(
                      "Defend successful: Removed last army from %s attackingCountry",
                      attackingCountry.getName()));
              display("attackingCountry has no armies left to attack");
              display("Ending battle, choose another attack move");
              gameMap.setCurrentContext(Context.GAME_ATTACK);
              return true;
            }
          }
        }
        if (!isAllOutEnabled) break;
      } else matchedCommand.ifPresent(command -> command.runOperation(gameMap, inputCommand));
    }
    return true;
  }
}
