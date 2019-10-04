package models;

public class Continent {
  private String color;
  private String name;
  private int value;

	public Continent(String color, String name, int value) {
		this.color = color;
		this.name = name;
		this.value = value;
  }

  public Continent(String name, int value) {
    this.color = "BLANK";
    this.name = name;
    this.value = value;
  }


  public String getColor() {
    return color;
  }

  public String getName() {
    return name;
  }

  public int getValue() {
    return value;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
	  return String.format("%s %d %s", this.name, this.value, this.color);
  }
}
