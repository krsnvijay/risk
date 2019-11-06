package models;

/**
 * This class maintains information for the cards
 *
 * @author Siddharth
 * @version 1.0
 */
public class Card {

  /**
   * Total cards introduced in the game
   */
  private static int cardCount = 0;

  /**
   * The name of the player who owns the card.
   */
  private String owner;

  /**
   * Stores the typeOfCard enum
   */
  private typeOfCard type;

  /**
   * Country of the card
   */
  private String country;

  /**
   * The constructor for a Card object.
   *
   * @param country Country associated with the card
   */
  public Card(String country) {
    super();
    // change the owner to deck during the start of the game
    this.owner = null;
    this.country = country;
    this.type = typeOfCard.values()[cardCount % 3];
    cardCount++;
  }

  /**
   * Returns the type of the card.
   *
   * @return typeOfCard with the type.
   */
  public typeOfCard getType() {
    return type;
  }

  /**
   * Returns the Card's and country's name.
   *
   * @return A string with name.
   */
  public String getName() {
    return getCountry() + " " + getType().value;
  }

  /**
   * Returns the Card's and country's name.
   *
   * @return A string with name.
   */
  public String getCardValue() {
    return getType().value;
  }

  /**
   * Returns the Country associated with the card
   *
   * @return Country object
   */
  public String getCountry() {
    return country;
  }

  /**
   * Sets the Country associated with the Card
   *
   * @param country Details of the country
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * Enum class to maintain the cards and its values
   */
  public enum typeOfCard {

    /**
     * The type of card (Artillery, Cavalry or Infantry)
     */
    INFANTRY("Infantry"),
    CAVALRY("Cavalry"),
    ARTILLERY("Artillery");

    public String value;

    /**
     * The constructor for a typeOfCard object
     *
     * @param value Number of armies for each card
     */
    typeOfCard(String value) {
      this.value = value;
    }
  }
}
