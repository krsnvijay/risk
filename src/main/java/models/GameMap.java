package models;

import java.util.*;
import java.util.stream.Collectors;

public class GameMap {
	private Map<String, ArrayList<String>> borders;
	private Map<String, Continent> continents;
	private Map<String, Country> countries;

  public GameMap(HashMap<String, ArrayList<String>> mapData) {
	  ArrayList<String> continentsInOrder =
			  mapData.get("[continents]").stream()
					  .map(line -> line.split(" ")[0])
					  .collect(Collectors.toCollection(ArrayList::new));
	  ArrayList<String> countriesInOrder =
        mapData.get("[countries]").stream()
				.map(line -> line.split(" ")[1])
            .collect(Collectors.toCollection(ArrayList::new));

    this.continents =
        mapData.get("[continents]").stream()
				.map(GameMap::mapContinent)
				.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
	  this.countries =
			  mapData.get("[countries]").stream()
					  .map(line -> GameMap.mapCountry(line, continentsInOrder))
					  .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    this.borders =
        mapData.get("[borders]").stream()
				.map(line -> GameMap.mapBorders(line, countriesInOrder))
				.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
  }

	public static Pair<String, Country> mapCountry(
			String countryLine, ArrayList<String> continentNames) {
		String[] splitCountryLine = countryLine.split(" ");
		String name = splitCountryLine[1];
		String continent = continentNames.get(Integer.parseInt(splitCountryLine[2]) - 1);
		Pair<String, Country> pair = new Pair<>(name, new Country(name, continent));
		return pair;
	}

	public static Pair<String, Continent> mapContinent(String continentLine) {
		String[] splitContinentLine = continentLine.split(" ");

		String name = splitContinentLine[0];
		int value = Integer.parseInt(splitContinentLine[1]);
		String color = splitContinentLine[2];
		Pair<String, Continent> pair = new Pair<>(name, new Continent(color, name, value));
		return pair;
	}

	public static Pair<String, ArrayList<String>> mapBorders(
			String borderLine, ArrayList<String> countryNames) {
		String[] splitBorderLine = borderLine.split(" ");
		String key = countryNames.get(Integer.parseInt(splitBorderLine[0]) - 1);
		ArrayList<String> value =
				Arrays.stream(splitBorderLine, 1, splitBorderLine.length)
						.map(str -> countryNames.get(Integer.parseInt(str) - 1))
            .collect(Collectors.toCollection(ArrayList::new));
		Pair<String, ArrayList<String>> pair = new Pair<>(key, value);
		return pair;
  }

	public static String showBorders(Map.Entry<String, ArrayList<String>> border) {
		return String.format(
				"%s %s", border.getKey(), border.getValue().stream().collect(Collectors.joining(" ")));
	}

  public void showMapByContinents() {
	  Map<String, List<Country>> groupedCountries =
			  this.countries.entrySet().stream()
					  .map(Map.Entry::getValue)
					  .collect(Collectors.groupingBy(Country::getContinent));
    groupedCountries.forEach(
        (k, v) ->
            System.out.printf(
                "[%s]\n%s\n\n",
					k, v.stream().map(Country::getName).sorted().collect(Collectors.joining("\n"))));
  }

	public String toMapFileFormat() {
		ArrayList<String> continentsOrder =
				this.continents.keySet().stream().sorted().collect(Collectors.toCollection(ArrayList::new));
		ArrayList<String> countriesOrder =
				this.countries.keySet().stream().sorted().collect(Collectors.toCollection(ArrayList::new));
		return String.format(
				"[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n",
				continentsOrder.stream()
						.map(str -> this.getContinents().get(str))
						.map(Continent::toString)
						.collect(Collectors.joining("\n")),
				countriesOrder.stream()
						.map(str -> this.getCountries().get(str))
						.map(
								c ->
										String.format(
												"%d %s %d",
												countriesOrder.indexOf(c.getName()) + 1,
												c.getName(),
												continentsOrder.indexOf(c.getContinent()) + 1))
						.collect(Collectors.joining("\n")),
				countriesOrder.stream()
						.map(
								str ->
										String.format(
												"%d %s",
												countriesOrder.indexOf(str) + 1,
												this.getBorders().get(str).stream()
														.map(c -> countriesOrder.indexOf(c) + 1)
														.map(String::valueOf)
														.collect(Collectors.joining(" "))))
						.collect(Collectors.joining("\n")));
  }

  @Override
  public String toString() {
    return String.format(
        "[continents]\n%s\n\n[countries]\n%s\n\n[borders]\n%s\n",
			this.continents.entrySet().stream()
					.map(Map.Entry::getValue)
					.map(Continent::toString)
					.collect(Collectors.joining("\n")),
			this.countries.entrySet().stream()
					.map(Map.Entry::getValue)
					.map(Country::toString)
					.collect(Collectors.joining("\n")),
			this.borders.entrySet().stream()
					.map(GameMap::showBorders)
					.collect(Collectors.joining("\n")));
  }

	public Map<String, ArrayList<String>> getBorders() {
		return borders;
	}

	public void setBorders(Map<String, ArrayList<String>> borders) {
		this.borders = borders;
	}

	public Map<String, Continent> getContinents() {
		return continents;
	}

	public void setContinents(Map<String, Continent> continents) {
		this.continents = continents;
	}

	public Map<String, Country> getCountries() {
		return countries;
	}

	public void setCountries(Map<String, Country> countries) {
		this.countries = countries;
  }
}
