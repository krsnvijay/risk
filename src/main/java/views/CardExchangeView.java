package views;

import models.Card;
import models.GameMap;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CardExchangeView implements Observer {

  /** Singleton CardExchangeView Instance * */
  private static CardExchangeView cardExchangeView = null;
  /** A cached Pool of threads to queue services on the UI thread * */
  private final ExecutorService service = Executors.newCachedThreadPool();
  /** Reference to the Runner instance on the UI thread * */
  private Runner appInstance;

  /**
   * Constructor for Card Exchange View
   *
   * @param app an instance of the Runner on the UI Thread
   */
  public CardExchangeView(Runner app) {
    this.appInstance = app;
    cardExchangeView = this;
  }

  /**
   * A getter for the singleton instance of CardExchangeView
   *
   * @return CardExchange View Singleton Instance
   */
  public static CardExchangeView getInstance() {
    return cardExchangeView;
  }

  /**
   * @param o an Observable Instance
   * @param arg an OPTIONAL argument to be passed from notifyObservers
   */
  @Override
  public void update(Observable o, Object arg) {
    GameMap gameMap = GameMap.getGameMap();
    List<Card> cardsInHand = gameMap.getCurrentPlayer().getStrategy().getCardsInHand();
    List<String> cardsInHandString =
        cardsInHand.stream()
            .map(
                (card) -> {
                  return String.format("%s %s", card.getCountry(), card.getType().name());
                })
            .collect(Collectors.toList());

    service.submit(() -> appInstance.updateCardLabelsTemp(cardsInHandString));
    if (gameMap.getCurrentContext().name().equals("GAME_REINFORCE"))
      service.submit(() -> appInstance.updateCardView());
  }
}
