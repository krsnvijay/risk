package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameMap {
	private ArrayList<Border> borders;
	private ArrayList<Continent> continents;
	private ArrayList<Country> countries;

	public GameMap(HashMap<String, ArrayList<String>> mapData) {
		this.countries = mapData.get("[countries]").stream().map(Country::new)
				.collect(Collectors.toCollection(ArrayList::new));
		this.continents = mapData.get("[continents]").stream().map(Continent::new)
				.collect(Collectors.toCollection(ArrayList::new));
		this.borders = mapData.get("[borders]").stream().map(Border::new)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public ArrayList<Border> getBorders() {
		return borders;
	}

	public ArrayList<Continent> getContinents() {
		return continents;
	}

	public ArrayList<Country> getCountries() {
		return countries;
	}

	public void setBorders(ArrayList<Border> borders) {
		this.borders = borders;
	}

	public void setContinents(ArrayList<Continent> continents) {
		this.continents = continents;
	}

	public void setCountries(ArrayList<Country> countries) {
		this.countries = countries;
	}

	public void showMapByContinents() {
		Map<Integer, List<Country>> groupedCountries = this.countries.stream()
				.collect(Collectors.groupingBy(Country::getContinent));
		groupedCountries.forEach((k, v) -> {
			System.out.printf("[%s]\n%s\n\n", this.continents.get(k - 1).getName(),
					v.stream().map(Country::toString).collect(Collectors.joining("\n")));
		});
	}

	@Override
	public String toString() {
		return String.format("[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n",
				this.continents.stream().map(Continent::toString).collect(Collectors.joining("\n")),
				this.countries.stream().map(Country::toString).collect(Collectors.joining("\n")),
				this.borders.stream().map(Border::toString).collect(Collectors.joining("\n")));

	}
}
