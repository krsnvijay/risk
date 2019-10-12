package models;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import utils.MapParser;

public class GameMapTest {
  GameMap gameMap;

  @Before
  public void setUp() throws Exception {
    // Load Risk map from resource folder
    File riskMap = new File(this.getClass().getResource("/risk.map").getFile());
    gameMap = MapParser.loadMap(riskMap.getPath());
  }

  @Test
  public void addContinent() {
    // Arrange
    String continentName = "Atlanta";
    int controlValue = 2;
    Continent continent = new Continent(continentName, controlValue);
    // Act
    gameMap.addContinent(continentName, controlValue);
    // Assert
    Collection<Continent> continents = gameMap.getContinents().values();
    assertThat(continents, hasItem(continent));
  }

  @Test
  public void removeContinent() {
    // Arrange
    String continentName = "Asia";
    // Act
    gameMap.removeContinent(continentName);
    // Assert
    Set<String> keySetOfContinentNames = gameMap.getContinents().keySet();
    assertThat(keySetOfContinentNames, not(hasItem(continentName)));

    Set<String> setOfContinentNamesInAllCountries =
        gameMap.getCountries().values().stream().map(Country::getContinent).collect(toSet());
    assertThat(setOfContinentNamesInAllCountries, not(hasItem(continentName)));

    Set<String> setOfContinentNamesInAllBorders =
        gameMap.getBorders().values().stream()
            .flatMap(Collection::stream)
            .map(gameMap.getCountries()::get)
            .map(Country::getContinent)
            .collect(toSet());
    assertThat(setOfContinentNamesInAllBorders, not(hasItem(continentName)));
  }

  @Test
  public void addCountry() {
    // Arrange
    String countryName = "Pandora";
    String continentName = "Asia";
    Country country = new Country(countryName, continentName);
    // Act
    gameMap.addCountry(countryName, continentName);
    // Assert
    Collection<Country> countries = gameMap.getCountries().values();
    assertThat(countries, hasItem(country));
  }

  @Test
  public void removeCountry() {
    // Arrange
    String countryName = "India";
    // Act
    gameMap.removeCountry(countryName);
    // Assert
    Set<String> keySetOfCountryNames = gameMap.getCountries().keySet();
    assertThat(keySetOfCountryNames, not(hasItem(countryName)));

    Set<String> keySetOfBorderCountryNames = gameMap.getBorders().keySet();
    assertThat(keySetOfBorderCountryNames, not(hasItem(countryName)));

    Set<String> setOfAllBorderCountryNames =
        gameMap.getBorders().values().stream()
            .flatMap(Collection::stream)
            .map(gameMap.getCountries()::get)
            .map(Country::getName)
            .collect(toSet());
    assertThat(setOfAllBorderCountryNames, not(hasItem(countryName)));
  }

  @Test
  public void addBorder() {
    // Arrange
    String countryName1 = "India";
    String countryName2 = "China";
    // Act
    gameMap.addBorder(countryName1, countryName2);
    // Assert
    Set<String> setOfCountry1NeighborNames = gameMap.getBorders().get(countryName1);
    assertThat(setOfCountry1NeighborNames, hasItem(countryName2));

    Set<String> setOfCountry2NeighborNames = gameMap.getBorders().get(countryName2);
    assertThat(setOfCountry2NeighborNames, hasItem(countryName1));
  }

  @Test
  public void removeBorder() {
    // Arrange
    String countryName1 = "India";
    String countryName2 = "China";
    // Act
    gameMap.removeBorder(countryName1, countryName2);
    // Assert
    Set<String> setOfCountry1NeighborNames = gameMap.getBorders().get(countryName1);
    assertThat(setOfCountry1NeighborNames, not(hasItem(countryName2)));

    Set<String> setOfCountry2NeighborNames = gameMap.getBorders().get(countryName2);
    assertThat(setOfCountry2NeighborNames, not(hasItem(countryName1)));
  }

  @Test
  public void removeCountryBorders() {
    // Arrange
    String countryName = "India";
    // Act
    gameMap.removeCountryBorders(countryName);
    // Assert
    Set<String> keySetOfBorderCountryNames = gameMap.getBorders().keySet();
    assertThat(keySetOfBorderCountryNames, not(hasItem(countryName)));

    Set<String> setOfNeighborCountryNames =
        gameMap.getBorders().values().stream()
            .flatMap(Collection::stream)
            .map(gameMap.getCountries()::get)
            .map(Country::getName)
            .collect(toSet());
    assertThat(setOfNeighborCountryNames, not(hasItem(countryName)));
  }
}
