package models;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import utils.MapParser;

public class GameMapTest {
  private GameMap gameMap;
  private String reason;

  @Before
  public void setUp() throws Exception {
    // Load Risk map from resource folder
    File riskMap = new File("src/test/resources/risk.map");
    gameMap = MapParser.loadMap(riskMap.getPath());
    reason = "";
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
    reason = continentName + " should exist in continents";
    Collection<Continent> continents = gameMap.getContinents().values();
    assertThat(reason, continents, hasItem(continent));
  }

  @Test
  public void removeContinent() {
    // Arrange
    String continentName = "Asia";
    // Act
    gameMap.removeContinent(continentName);
    // Assert
    reason = continentName + " should not exist in continent keys";
    Set<String> keySetOfContinentNames = gameMap.getContinents().keySet();
    assertThat(reason, keySetOfContinentNames, not(hasItem(continentName)));

    reason = "All countries belonging to continent - " + continentName + " should not exist";
    Set<String> setOfContinentNamesInAllCountries =
        gameMap.getCountries().values().stream().map(Country::getContinent).collect(toSet());
    assertThat(reason, setOfContinentNamesInAllCountries, not(hasItem(continentName)));

    reason =
        "All countries belonging to continent - " + continentName + " should not exist in borders";
    Set<String> setOfContinentNamesInAllBorders =
        gameMap.getBorders().values().stream()
            .flatMap(Collection::stream)
            .map(gameMap.getCountries()::get)
            .map(Country::getContinent)
            .collect(toSet());
    assertThat(reason, setOfContinentNamesInAllBorders, not(hasItem(continentName)));
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
    reason = countryName + " should exist in countries";
    Collection<Country> countries = gameMap.getCountries().values();
    assertThat(reason, countries, hasItem(country));
  }

  @Test
  public void removeCountry() {
    // Arrange
    String countryName = "India";
    // Act
    gameMap.removeCountry(countryName);
    // Assert
    reason = countryName + " should not exist in country keys";
    Set<String> keySetOfCountryNames = gameMap.getCountries().keySet();
    assertThat(reason, keySetOfCountryNames, not(hasItem(countryName)));

    reason = countryName + " should not exist in border keys";
    Set<String> keySetOfBorderCountryNames = gameMap.getBorders().keySet();
    assertThat(reason, keySetOfBorderCountryNames, not(hasItem(countryName)));

    reason = countryName + " should not exist as a neighbor in borders";
    Set<String> setOfAllBorderCountryNames =
        gameMap.getBorders().values().stream()
            .flatMap(Collection::stream)
            .map(gameMap.getCountries()::get)
            .map(Country::getName)
            .collect(toSet());
    assertThat(reason, setOfAllBorderCountryNames, not(hasItem(countryName)));
  }

  @Test
  public void addBorder() {
    // Arrange
    String countryName1 = "India";
    String countryName2 = "China";
    // Act
    gameMap.addBorder(countryName1, countryName2);
    // Assert
    reason = countryName1 + " must have a neighbor " + countryName2;
    Set<String> setOfCountry1NeighborNames = gameMap.getBorders().get(countryName1);
    assertThat(reason, setOfCountry1NeighborNames, hasItem(countryName2));

    reason = countryName2 + " must have a neighbor " + countryName1;
    Set<String> setOfCountry2NeighborNames = gameMap.getBorders().get(countryName2);
    assertThat(reason, setOfCountry2NeighborNames, hasItem(countryName1));
  }

  @Test
  public void removeBorder() {
    // Arrange
    String countryName1 = "India";
    String countryName2 = "China";
    // Act
    gameMap.removeBorder(countryName1, countryName2);
    // Assert
    reason = countryName1 + " must not have a neighbor " + countryName2;
    Set<String> setOfCountry1NeighborNames = gameMap.getBorders().get(countryName1);
    assertThat(reason, setOfCountry1NeighborNames, not(hasItem(countryName2)));

    reason = countryName1 + " must not have a neighbor " + countryName2;
    Set<String> setOfCountry2NeighborNames = gameMap.getBorders().get(countryName2);
    assertThat(reason, setOfCountry2NeighborNames, not(hasItem(countryName1)));
  }

  @Test
  public void removeCountryBorders() {
    // Arrange
    String countryName = "India";
    // Act
    gameMap.removeCountryBorders(countryName);
    // Assert
    reason = countryName + " must not exist in country keys";
    Set<String> keySetOfBorderCountryNames = gameMap.getBorders().keySet();
    assertThat(reason, keySetOfBorderCountryNames, not(hasItem(countryName)));

    reason = countryName + " must not exist as a neighbor in borders";
    Set<String> setOfNeighborCountryNames =
        gameMap.getBorders().values().stream()
            .flatMap(Collection::stream)
            .map(gameMap.getCountries()::get)
            .map(Country::getName)
            .collect(toSet());
    assertThat(reason, setOfNeighborCountryNames, not(hasItem(countryName)));
  }
}
