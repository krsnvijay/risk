package models;

/**
 * @author v_valla
 *
 */
public class Country {

	private static int counter = 0;
	private int continent;
	private int id;
	private String name;

	public Country(int id, String name, int continent) {
		super();
		this.id = id;
		this.name = name;
		this.continent = continent;
	}

	public Country(String countryLine) {
		String[] splitCountryLine = countryLine.split(" ");
		counter += 1;
		this.id = Integer.parseInt(splitCountryLine[0]);
		this.name = splitCountryLine[1];
		this.continent = Integer.parseInt(splitCountryLine[2]);
	}
	
	public static int getCounter() {
		return counter;
	}

	public static void setCounter(int counter) {
		Country.counter = counter;
	}

	public int getContinent() {
		return this.continent;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setContinent(int continent) {
		this.continent = continent;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("%d %s %d", this.id, this.name, this.continent);
	}
}