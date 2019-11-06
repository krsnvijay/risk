package models;

import java.util.Objects;

/**
 * This class maintains the information for a Country.
 *
 * @author Vijay
 * @version 1.0
 */
public class Country {
  /** The name of the continent the Country belongs to. */
  private String continent;

  /** The name of the Country. */
  private String name;

  /** The x coordinate for the Country (serialization). */
  private int x;

  /** The y coordinate for the Country (serialization). */
  private int y;

  /** The name of the Player who owns the Country. */
  private String ownerName;

  /** Maintains the number of armies currently in a country. */
  private int numberOfArmies;

  /**
   * Joins the cards to the countries
   */
  private Card card;

  /**
   * This boolean checks whether the player needs to get a country's card. When the player captures
   * the country, it is set to true. It won't be set to true in any other scenario.
   */
  private boolean playerNeedsCardAdded = false;

  /** Getter and setter for the Card object */
  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }

  /**
   * The constructor for the Country class.
   *
   * @param name The name of the Country.
   * @param continent The name of the Continent it belongs to.
   */
  public Country(String name, String continent) {
    super();
    this.name = name;
    this.continent = continent;
    this.x = 0;
    this.y = 0;
  }

  /**
   * The constructor for the Country class.
   *
   * @param name The name of the Country.
   * @param continent The name of the Continent it belongs to.
   * @param x The x coordinate of the country.
   * @param y The y coordinate of teh country.
   */
  public Country(String name, String continent, int x, int y) {
    super();
    this.name = name;
    this.continent = continent;
    this.x = x;
    this.y = y;
  }

  /**
   * Returns the name of the continent.
   *
   * @return String with the name.
   */
  public String getContinent() {
    return this.continent;
  }

  /**
   * Sets the name of the continent
   *
   * @param continent String with the name.
   */
  public void setContinent(String continent) {
    this.continent = continent;
  }

  /**
   * Returns the name of the country.
   *
   * @return String with the name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the country.
   *
   * @param name String with the name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the x coordinate.
   *
   * @return Integer with the x coordinate.
   */
  public int getX() {
    return this.x;
  }

  /**
   * Sets the x coordinate.
   *
   * @param x An integer with the x coordinate.
   */
  public void setX(int x) {
    this.x = x;
  }

  /**
   * Gets the y coordinate.
   *
   * @return Integer with the y coordinate.
   */
  public int getY() {
    return this.y;
  }

  /**
   * Sets the y coordinate.
   *
   * @param y An integer with the y coordinate.
   */
  public void setY(int y) {
    this.y = y;
  }

  /**
   * Returns the name of the owner.
   *
   * @return A String with the name.
   */
  public String getOwnerName() {
    return this.ownerName;
  }

  /**
   * Sets the name of the owner of the country.
   *
   * @param ownerName String with the name.
   */
  public void setOwnerName(String ownerName) {
    WorldDomination.getInstance().recomputeAttributes();
    this.ownerName = ownerName;
  }

  /** Pretty prints the Country object. */
  @Override
  public String toString() {
    return String.format("%s (%s)", this.name, this.continent);
  }

  /** @return String a formatted print of countries by ownership. */
  public String showCountryByOwnership() {
    return String.format(
        "%s(%s:%s:%d)", this.name, this.continent, this.ownerName, this.numberOfArmies);
  }

  /**
   * Returns the number of armies
   *
   * @return An integer with the number of armies
   */
  public int getNumberOfArmies() {
    return numberOfArmies;
  }

  /**
   * Sets the number of armies
   *
   * @param numberOfArmies number of armies
   */
  public void setNumberOfArmies(int numberOfArmies) {
    this.numberOfArmies = numberOfArmies;
    WorldDomination.getInstance().recomputeAttributes();
  }

  /**
   * Adds armies to the country
   *
   * @param count an Integer to add to the current armies of a country
   * @return true if armies can be added
   */
  public boolean addArmies(int count) {
    if (numberOfArmies + count > 0) {
      this.numberOfArmies += count;
      WorldDomination.getInstance().recomputeAttributes();
      return true;
    }
    return false;
  }

  /**
   * Removes armies from a country
   *
   * @param count an Integer to remove from the current armies of a country
   * @return true if armies can be removed
   */
  public boolean removeArmies(int count) {
    if (numberOfArmies - count > 0) {
      this.numberOfArmies -= count;
      WorldDomination.getInstance().recomputeAttributes();
      return true;
    }
    return false;
  }

  /** Checks if a Country object is equal to another. */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Country country = (Country) o;
    return x == country.x
        && y == country.y
        && numberOfArmies == country.numberOfArmies
        && continent.equals(country.continent)
        && name.equals(country.name)
        && Objects.equals(ownerName, country.ownerName);
  }

  /** @return int hash code for the Country object. */
  @Override
  public int hashCode() {
    return Objects.hash(continent, name, x, y, ownerName, numberOfArmies);
  }
}
