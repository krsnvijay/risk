package models;

public class Country {
	private String continent;
  private String name;

	public Country(String name, String continent) {
    super();
    this.name = name;
    this.continent = continent;
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

  @Override
  public String toString() {
	  return String.format("%s %s", this.name, this.continent);
  }
}
