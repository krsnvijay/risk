package models.state;

import models.Card;

import java.util.ArrayList;

public class PlayerState {
  /** Stores the strategy of the player */
  private String strategy;
  /** This instance variable holds the name of the player. */
  private String playerName;
  /** Stores the number of armies a player has. */
  private int numberOfArmies;
  /** Stores the cards currently held by the player. */
  private ArrayList<Card> cardsInHand = new ArrayList<>();

  private PlayerState() {
  }

  public String getStrategy() {
    return strategy;
  }

  public String getPlayerName() {
    return playerName;
  }

  public int getNumberOfArmies() {
    return numberOfArmies;
  }

  public ArrayList<Card> getCardsInHand() {
    return cardsInHand;
  }

  public void setStrategy(String strategy) {
    this.strategy = strategy;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public void setNumberOfArmies(int numberOfArmies) {
    this.numberOfArmies = numberOfArmies;
  }

  public void setCardsInHand(ArrayList<Card> cardsInHand) {
    this.cardsInHand = cardsInHand;
  }

  public static class PlayerStateBuilder {
    private String strategy;
    private String playerName;
    private int numberOfArmies;
    private ArrayList<Card> cardsInHand;

    private PlayerStateBuilder() {
    }

    public static PlayerStateBuilder aPlayerState() {
      return new PlayerStateBuilder();
    }

    public PlayerStateBuilder withStrategy(String strategy) {
      this.strategy = strategy;
      return this;
    }

    public PlayerStateBuilder withPlayerName(String playerName) {
      this.playerName = playerName;
      return this;
    }

    public PlayerStateBuilder withNumberOfArmies(int numberOfArmies) {
      this.numberOfArmies = numberOfArmies;
      return this;
    }

    public PlayerStateBuilder withCardsInHand(ArrayList<Card> cardsInHand) {
      this.cardsInHand = cardsInHand;
      return this;
    }

    public PlayerState build() {
      PlayerState playerState = new PlayerState();
      playerState.setStrategy(strategy);
      playerState.setPlayerName(playerName);
      playerState.setNumberOfArmies(numberOfArmies);
      playerState.setCardsInHand(cardsInHand);
      return playerState;
    }
  }
}
