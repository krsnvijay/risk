package utils;

import models.Card;
import models.Context;
import models.Continent;
import models.Country;
import models.state.GameState;
import models.state.PlayerState;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public abstract class GameStateBuilder {
  protected GameState gameState;

  public GameState getGameState() {
    return gameState;
  }

  public void createGameState() {
    gameState = new GameState();
  }

  abstract void buildPlayersList();

  abstract void buildDeck();

  abstract void buildBorders();

  abstract void buildContinents();

  abstract void buildCountries();

  abstract void buildCurrentContext();

  abstract void buildCurrentPlayerIndex();

  abstract void buildNumberOfTradedSet();

  abstract void buildArmiesTradedForSet();
}
