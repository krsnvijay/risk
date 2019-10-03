package models;

public class Continent {
	static int counter = 0;
	int id;
	String name;
	int value;
	String color;

	public Continent(String continentLine) {
		String[] splitContinentLine = continentLine.split(" ");
		counter += 1;
		this.id = counter;
		this.name = splitContinentLine[0];
		this.value = Integer.parseInt(splitContinentLine[1]);
		this.color = splitContinentLine[2];
	}
	@Override
	public String toString() {
		return String.format("%d %s %d %s",this.id,this.name,this.value,this.color);
	}
}
