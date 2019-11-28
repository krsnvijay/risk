package controllers;

import models.*;
import models.player.Player;
import models.player.PlayerHuman;
import models.player.PlayerStrategy;
import utils.CLI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.reverseOrder;
import static java.util.stream.Collectors.toCollection;
import static views.ConsoleView.display;

/**
 * This is the controller that manages the Attack phase entirely.
 *
 * @version 1.0
 * @author Vijay
 */
public class BattleController {

  /** The number of dice for the attacker. */
  private int numOfDiceAttacker;

  /** The number of dice for the defender. */
  private int numOfDiceDefender;

  /** The attacking Country object. */
  private Country attackingCountry;

  /** The defending Country object. */
  private Country defendingCountry;

  /** The name of the attacking player. */
  private String attackerName;

  /** The name of the defending player. */
  private String defenderName;

  /** The defending player. */
  private Player defendingPlayer;

  /** The attacking player. */
  private Player attackingPlayer;

  /** An instance of the Game Map. */
  private GameMap gameMap;

  /** The topmost card on the deck. */
  private Card topCard;

  /** The number of armies to move after winning a battle */
  private int numOfArmiesToMove = 0;

  /** Tracks if -allout is enabled. */
  private boolean isAllOutEnabled = false;

  /** Disable input from the user, flag used for unit tests */
  private boolean isNoInputEnabled = false;

  /**
   * Constructor for the Battle Controller
   *
   * @param gameMap the Game Map object.
   * @param command the command to be processed.
   */
  public BattleController(GameMap gameMap, String command) {
    this.gameMap = gameMap;
    String[] commandSplit = command.split(" ");
    attackingCountry = gameMap.getCountries().get(commandSplit[1]);
    attackerName = gameMap.getCurrentPlayer().getStrategy().getPlayerName();
    defendingCountry = gameMap.getCountries().get(commandSplit[2]);
    if (!command.contains("-allout")) {
      numOfDiceAttacker = Integer.parseInt(commandSplit[3]);
    }
    defendingPlayer =
        gameMap.getPlayersList().stream()
            .filter(
                player ->
                    player.getStrategy().getPlayerName().equals(defendingCountry.getOwnerName()))
            .findFirst()
            .get();
    attackingPlayer = gameMap.getCurrentPlayer();
    defendingPlayer =
        gameMap.getPlayersList().stream()
            .filter(
                player ->
                    player.getStrategy().getPlayerName().equals(defendingCountry.getOwnerName()))
            .findFirst()
            .get();
    defenderName = defendingPlayer.getStrategy().getPlayerName();
    isAllOutEnabled = command.contains("-allout");
  }

  /**
   * Checks if player has atleast 2 armies in any of the countries they own
   *
   * @param attackerName name of the player that's attacking
   * @param gameMap contains game state
   * @return true if attack move is possible otherwise returns false
   */
  public static boolean isAttackOrFortifyMovePossible(String attackerName, GameMap gameMap) {
    return Player.getCountriesByOwnership(attackerName, gameMap).stream()
        .mapToInt(Country::getNumberOfArmies)
        .anyMatch(armyCount -> armyCount > 1);
  }

  /**
   * Calculates the maximum number of dice for a defender.
   *
   * @return integer representing the number of dice.
   */
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

  /**
   * Calculates the maximum number of dice for an attacker.
   *
   * @return integer representing the number of dice.
   */
  public int calculateMaxDiceForAttacker() {
    int armies = attackingCountry.getNumberOfArmies();
    if (armies > 3) {
      return 3;
    } else if (armies > 1) {
      return armies - 1;
    } else {
      return 0;
    }
  }

  /**
   * This is a utility method that compares the dice rolls according to the Risk rules.
   *
   * @param attackerDiceRoll the rolls for the attacker
   * @param defenderDiceRoll the rolls for the defender
   * @return the winner of the dice roll.
   */
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
          String.format(
              "comparing dice %d (attacker) with %d (defender)", maxAttacker, maxDefender),
          true);
      boolean result = maxAttacker > maxDefender;
      display(String.format("%s won!", result ? "Attacker" : "Defender"), true);
      diceComparisonResult.add(result);
    }
    return diceComparisonResult;
  }

  /**
   * Rolls multiple dice.
   *
   * @param numOfDice number of dice to roll
   * @return result of each dice roll
   */
  public ArrayList<Integer> rollDice(int numOfDice) {
    Supplier<Integer> roll = () -> (int) (Math.random() * 6) + 1;
    return Stream.generate(roll).limit(numOfDice).collect(toCollection(ArrayList::new));
  }

  /**
   * Initiates the attack phase.
   *
   * @return true when the attack finishes.
   */
  public boolean startBattle() {
    gameMap.setCurrentContext(Context.GAME_ATTACK_BATTLE_DEFENDER);
    if (isNoInputEnabled) {
      display(
          String.format(
              "%s owned by %s declared an attack on %s owned by %s",
              attackingCountry.getName(), attackerName, defendingCountry.getName(), defenderName),
          true);
    }
    if (isAllOutEnabled) {
      display("Attacker enabled allout, will always choose max dice", true);
      while (gameMap.getCurrentContext() != Context.GAME_ATTACK
          && attackingCountry.getNumberOfArmies() > 1) {
        attemptAttack();
        if (GameController.isTournament && GameMap.isGameOver) {
          return true;
        }
      }
    } else {
      attemptAttack();
      display("Choose another attack move or move to next phase", true);
      gameMap.setCurrentContext(Context.GAME_ATTACK);
    }

    if (attackingCountry.getNumberOfArmies() == 1
        && !isAttackOrFortifyMovePossible(attackerName, gameMap)) {
      display("Moving to next phase, No attack move possible", true);
      GameController.assignedCard = false;
      PlayerStrategy strategy = gameMap.getCurrentPlayer().getStrategy();
      if (strategy instanceof PlayerHuman) {
        GameController.changeToNextPhase(gameMap);
      }
    }
    return true;
  }

  /**
   * Returns the number of dice for the attacker.
   *
   * @return number of dice.
   */
  public int getNumOfDiceFromAttacker() {
    if (isAllOutEnabled) {
      return calculateMaxDiceForAttacker();
    } else {
      return numOfDiceAttacker;
    }
  }

  /**
   * Returns the number of dice for the defender.
   *
   * @return number of dice.
   */
  public int getNumOfDiceFromDefender() {
    if (isAllOutEnabled) {
      return calculateMaxDiceForDefender();
    }
    if (!(defendingPlayer.getStrategy() instanceof PlayerHuman)) {
      return calculateMaxDiceForDefender();
    }
    if (isNoInputEnabled) {
      display("Skipping Input loop from defender", true);
      isNoInputEnabled = false;
      return numOfDiceDefender;
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
        numOfDiceDefender = Integer.parseInt(commandSplit[1]);
        // Defender can only use 1 or 2 dice
        if (numOfDiceDefender != 1 && numOfDiceDefender != 2) {
          display("Error: Defender can only defend with 1 or 2 Dice", false);
          continue;
        }
        // Need atleast 2 armies to use 2 Dice
        if (numOfDiceDefender == 2 && defendingCountry.getNumberOfArmies() < 2) {
          display("Error: Need atleast 2 armies to use 2 Dice", false);
          continue;
        }
        return numOfDiceDefender;
      } else {
        matchedCommand.ifPresent(command -> command.runOperation(gameMap, inputCommand));
      }
    }
  }

  /** Attempts an attack based on the command specified by the attacker. */
  public void attemptAttack() {
    numOfDiceDefender = getNumOfDiceFromDefender();
    numOfDiceAttacker = getNumOfDiceFromAttacker();

    // Simulate Dice Roll
    display(String.format("Rolling %d dice for attacker", numOfDiceAttacker), false);
    ArrayList<Integer> attackerDiceRoll = rollDice(numOfDiceAttacker);
    display(String.format("Rolling %d dice for defender", numOfDiceDefender), false);
    ArrayList<Integer> defenderDiceRoll = rollDice(numOfDiceDefender);
    display(
        String.format("%s (attacker) rolled : %s", attackerName, attackerDiceRoll.toString()),
        true);
    display(
        String.format("%s (defender) rolled : %s", defenderName, defenderDiceRoll.toString()),
        true);

    // Compare diceRoll results
    ArrayList<Boolean> results = compareDiceRolls(attackerDiceRoll, defenderDiceRoll);
    for (boolean result : results) {
      if (result) {
        successfulAttack();
        if (GameController.isTournament && GameMap.isGameOver) {
          return;
        }
      } else {
        successfulDefence();
      }
      if (gameMap.getCurrentContext() == Context.GAME_ATTACK) {
        break;
      }
    }
  }

  /**
   * Returns the armies to be moved to a captured territory on successful attack.
   *
   * @return number of armies as an integer.
   */
  public int getNumOfArmiesToMoveFromAttacker() {
    if (isNoInputEnabled) {
      isNoInputEnabled = false;
      Predicate<Integer> validateNumOfArmiesToMove =
          (armies) -> armies > numOfDiceAttacker && armies <= attackingCountry.getNumberOfArmies();
      if (!(attackingPlayer.getStrategy() instanceof PlayerHuman)) {
        // Attack
        if (validateNumOfArmiesToMove.test(attackingCountry.getNumberOfArmies() / 2)) {
          numOfArmiesToMove = attackingCountry.getNumberOfArmies() / 2;
        } else numOfArmiesToMove = numOfDiceAttacker;
      }
      return numOfArmiesToMove;
    }
    while (true) {
      display(
          String.format(
              "%s has %d armies, Choose numOfArmiesToMove to move to new territory %s",
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
        numOfArmiesToMove = Integer.parseInt(commandSplit[1]);
        // num of armies should be > 0 and < available armies
        if (numOfArmiesToMove < numOfDiceAttacker
            || numOfArmiesToMove >= attackingCountry.getNumberOfArmies()) {
          display(
              String.format(
                  "Error: Num of armies to move should be >= %d (num of dice used in attack) and < available armies",
                  numOfDiceAttacker),
              false);
          continue;
        }
        return numOfArmiesToMove;
      } else {
        matchedCommand.ifPresent(command -> command.runOperation(gameMap, inputCommand));
      }
    }
  }

  /** This method executes on successful capture of a country. */
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

  /** This method executes on successful defence of a country. */
  public void successfulDefence() {
    if (attackingCountry.getNumberOfArmies() > 2) {
      attackingCountry.removeArmies(1);
      display(
          String.format(
              "Defend successful: Removed 1 army from %s (attackingCountry)",
              attackingCountry.getName()),
          true);
      display("Remaining armies in defendingCountry " + defendingCountry.getNumberOfArmies(), true);
    } else if (attackingCountry.getNumberOfArmies() == 2) {
      attackingCountry.removeArmies(1);
      display(
          String.format(
              "Defend successful: %s(attackingCountry) has only one army and can't attack",
              attackingCountry.getName()),
          true);
      display("Ending battle, choose another attack move", true);
      gameMap.setCurrentContext(Context.GAME_ATTACK);
    }
  }

  /** This method executes on a specific roll-win for attacker. */
  public void successfulAttack() {
    Player winningPlayer = gameMap.getCurrentPlayer();
    ArrayList<Player> losingPlayer =
        gameMap.getPlayersList().stream()
            .filter(c -> c.getStrategy().getPlayerName().equals(defenderName))
            .collect(Collectors.toCollection(ArrayList::new));
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
      if (!GameController.assignedCard) {
        gameMap.assignCard();
        GameController.assignedCard = true;
        PlayerStrategy strategy = gameMap.getCurrentPlayer().getStrategy();
        display(
            strategy.getPlayerName()
                + " currently has "
                + strategy.getCardsInHand().stream()
                    .map(Card::getName)
                    .collect(Collectors.joining(" "))
                + " card(s).",
            false);
      }

      boolean isDefenderHavingCountries =
          Player.checkPlayerOwnsAtleastOneCountry(defenderName, gameMap);
      if (!isDefenderHavingCountries) {
        display(String.format("Removing %s from the game due to no ownership", defenderName), true);
        if (!losingPlayer.isEmpty()) {
          ArrayList<Card> losingPlayerCards = losingPlayer.get(0).getStrategy().getCardsInHand();
          for (Card losingPlayerCard : losingPlayerCards) {
            winningPlayer.addCard(losingPlayerCard);
          }
          display(String.format("%s now owns %s's cards", attackerName, defenderName), true);
        }
        gameMap.removeGamePlayer(defenderName);
      }

      // Check Game Victory condition
      if (Player.checkPlayerOwnsAllTheCountries(attackerName, gameMap)) {
        // Player Won the Game, Exit
        display(String.format("%s(attacker) won the game!", attackerName), true);
        GameMap.isGameOver = true;
        if (GameController.isTournament) {
          return;
        } else System.exit(0);
      } else {
        // Change to initial attack phase where player can choose to battle again
        display("Ending battle, choose another attack move", true);
        gameMap.setCurrentContext(Context.GAME_ATTACK);
      }
    }
  }

  /**
   * Setter for disabling or enabling user input
   *
   * @param isNoInputEnabled set to true to disable input
   */
  public void setNoInputEnabled(boolean isNoInputEnabled) {
    this.isNoInputEnabled = isNoInputEnabled;
  }

  /**
   * Setter for num of Dice for defender
   *
   * @param numOfDiceDefender number of dice to roll for the defender
   */
  public void setNumOfDiceDefender(int numOfDiceDefender) {
    this.numOfDiceDefender = numOfDiceDefender;
  }

  /**
   * Setter for num of Armies To Move
   *
   * @param numOfArmiesToMove number of armies to move after winning a battle
   */
  public void setNumOfArmiesToMove(int numOfArmiesToMove) {
    this.numOfArmiesToMove = numOfArmiesToMove;
  }
}
