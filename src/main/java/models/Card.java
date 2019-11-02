package models;

/**
 * This class maintains information for the cards
 *
 * @author Siddharth Singh
 */

public class Card {

    /** The owner of the card */
    private Player owner;

   /** Stores the Card object */
    private typeOfCard type;

    /** Country of the card */
    private Country country;

    /** Total cards introduced in the game */
    private static int cardCount = 0;

    /**
     * Returns the Player who owns the card.
     *
     * @return Details of player
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Sets Player for the card
     *
     * @param owner String with the name.
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Returns the Country associated with the card
     *
     * @return Country object
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Sets the Country associated with the Card
     *
     * @param country Details of the country
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * Enum class to maintain the cards and its values
     *
     */
    public enum typeOfCard {

        /** The type of card (Artillery, Cavalry or Infantry) */
        INFANTRY(1),
        CAVALRY(5),
        ARTILLERY(10);

        public int value;

        /**
         * The constructor for a typeOfCard object
         *
         * @param value Number of armies for each card
         */
        private typeOfCard(int value) {
            this.value = value;
        }
    }

    /**
     * The constructor for a Card object.
     *
     * @param owner Player who owns the card
     * @param country Country associated with the card
     *
     */
    public Card(Player owner, Country country) {
        super();
        this.owner = owner;
        this.country = country;
        this.type = typeOfCard.values()[cardCount%3];
        cardCount++;
    }

}