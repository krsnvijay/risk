package views;

import models.Card;
import models.GameMap;
import models.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CardExchangeView implements Observer {

  private final ExecutorService service = Executors.newCachedThreadPool();
  private Runner appInstance;

  private static CardExchangeView cardExchangeView = null;

  public static CardExchangeView getInstance() {
    return cardExchangeView;
  }

  public CardExchangeView(Runner app) {
    this.appInstance = app;
    cardExchangeView = this;
  }

  @Override
  public void update(Observable o, Object arg) {
    Player player = (Player) o;
    System.out.println("CALLED HERE");
    // an update to cardsInHand for current player
    GameMap gameMap = GameMap.getGameMap();
    List<Card> cardsInHand = gameMap.getCurrentPlayer().getCardsInHand();
    List<String> cardsInHandString = cardsInHand.stream().map((card) -> {
      return String.format("%s %s", card.getCountry(), card.getType().name());
    }).collect(Collectors.toList());

    service.submit(() -> appInstance.updateCardLabelsTemp(cardsInHandString));
  }
}
