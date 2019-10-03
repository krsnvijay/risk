package models;

public class Country {
	static int counter = 0;
	int id;
	String name;
	int continent;

	public Country(String countryLine) {
		String[] splitCountryLine = countryLine.split(" ");
		counter += 1;
		this.id = Integer.parseInt(splitCountryLine[0]);
		this.name = splitCountryLine[1];
		this.continent = Integer.parseInt(splitCountryLine[2]);
	}

	@Override
	public String toString() {
		return String.format("%d %s %d", this.id, this.name, this.continent);
	}

	public int getContinent() {
		return this.continent;
	}
}