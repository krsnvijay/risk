package models;

public class Continent {
  private static int counter = 0;
  private String color;
  private int id;
  private String name;
  private int value;

  public Continent(String continentLine) {
    String[] splitContinentLine = continentLine.split(" ");
    counter += 1;
    this.id = counter;
    this.name = splitContinentLine[0];
    this.value = Integer.parseInt(splitContinentLine[1]);
    this.color = splitContinentLine[2];
  }
  
  public Continent(String continent,String control_value){
		counter+=1;
		this.id=counter;
		this.name=continent;
		this.value=Integer.parseInt(control_value);
		this.color = "BLANK";
  }

  public static int getCounter() {
    return counter;
  }

  public static void setCounter(int counter) {
    Continent.counter = counter;
  }

  public String getColor() {
    return color;
  }

  public int getId() {
    return id;
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

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.format("%d %s %d %s", this.id, this.name, this.value, this.color);
  }
}
