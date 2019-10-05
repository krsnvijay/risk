package models;

public class Country {
  private String continent;
  private String name;
  private int x;
  private int y;
  private String ownerName;


  public Country(String name, String continent) {
    super();
    this.name = name;
    this.continent = continent;
    this.x = 0;
    this.y = 0;
  }

  public Country(String name, String continent, int x, int y) {
    super();
    this.name = name;
    this.continent = continent;
    this.x = x;
    this.y = y;
  }

  public String getContinent() {
    return this.continent;
  }

  public String getName() {
    return name;
  }

  public void setContinent(String continent) {
    this.continent = continent;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getOwnerName() {
    return this.ownerName;
  }

  @Override
  public String toString() {
    return String.format("%s %s", this.name, this.continent);
  }
}
