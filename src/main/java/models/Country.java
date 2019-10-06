package models;

/**
 * This class maintains the information for a Country.
 * 
 * @author Vijay
 *
 */
/**
 * @author bansa
 *
 */
public class Country {
  /**
   * The name of the continent the Country belongs to.
   */
  private String continent;

  /**
   * The name of the Country.
   */
  private String name;

  /**
   * The x coordinate for the Country (serialization).
   */
  private int x;

  /**
   * The y coordinate for the Country (serialization).
   */
  private int y;

  /**
   * The name of the Player who owns the Country.
   */
  private String ownerName;

  private int numberOfArmies;


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
   * Returns the name of the country.
   * 
   * @return String with the name.
   */
  public String getName() {
    return name;
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
   * Gets the y coordinate.
   * 
   * @return Integer with the y coordinate.
   */
  public int getY() {
    return this.y;
  }

  /**
   * Sets the name of the owner of the country.
   * 
   * @param ownerName String with the name.
   */
  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
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
   * Pretty prints the Country object.
   */
  @Override
  public String toString() {
    return String.format("%s %s", this.name, this.continent);
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
   * Sets the y coordinate.
   * 
   * @param y An integer with the y coordinate.
   */
  public void setY(int y) {
    this.y = y;
  }

  /**
   * @return
   */
  public int getNumberOfArmies() {
    return numberOfArmies;
  }

  /**
   * @param numberOfArmies
   */
  public void setNumberOfArmies(int numberOfArmies) {
    this.numberOfArmies = numberOfArmies;
  }

  /**
   * @param count
   */
  public void addArmies(int count) {
    this.numberOfArmies += count;
  }

  /**
   * @param count
   */
  public void removeArmies(int count) {
    this.numberOfArmies -= count;
  }
}
