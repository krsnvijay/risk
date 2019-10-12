package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import utils.MapParser;

public class GameMapTest {
  GameMap gameMap;

  @Before
  public void setUp() throws Exception {
    File riskMap = new File(this.getClass().getResource("/risk.map").getFile());
    gameMap = MapParser.loadMap(riskMap.getPath());
  }

  @Test
  public void addContinent() {
    String continentName = "Atlanta";
    int controlValue = 2;
    Continent continent = new Continent(continentName, controlValue);

    gameMap.addContinent(continentName, controlValue);

    assertEquals(gameMap.getContinents().get(continentName), continent);
  }

  @Test
  public void removeContinent() {
    String continentName = "Asia";

    gameMap.removeContinent(continentName);

    boolean isContinentRemovedInGameMap = !gameMap.getContinents().containsKey(continentName);
    assertTrue(isContinentRemovedInGameMap);

    boolean isContinentRemovedInCountries =
        gameMap.getCountries().values().stream()
            .map(Country::getContinent)
            .distinct()
            .noneMatch(continent -> continent.equals(continentName));
    assertTrue(isContinentRemovedInCountries);

    boolean isContinentRemovedInBorders =
        gameMap.getBorders().values().stream()
            .flatMap(Collection::stream)
            .map(gameMap.getCountries()::get)
            .map(Country::getContinent)
            .distinct()
            .noneMatch(continent -> continent.equals(continentName));
    assertTrue(isContinentRemovedInBorders);
  }

  @Test
  public void addCountry() {
    String countryName = "Pandora";
    String continentName = "Asia";
    Country country = new Country(countryName, continentName);

    gameMap.addCountry(countryName, continentName);

    assertEquals(gameMap.getCountries().get(countryName), country);
  }

  @Test
  public void removeCountry() {
    String countryName = "India";

    gameMap.removeCountry(countryName);

    boolean isCountryRemovedInGameMap = !gameMap.getCountries().containsKey(countryName);
    assertTrue(isCountryRemovedInGameMap);

    boolean isCountryRemovedInBorders = !gameMap.getBorders().containsKey(countryName);
    assertTrue(isCountryRemovedInBorders);

    boolean isCountryNotANeighbor =
        gameMap.getBorders().values().stream()
            .flatMap(Collection::stream)
            .map(gameMap.getCountries()::get)
            .map(Country::getName)
            .distinct()
            .noneMatch(country -> country.equals(countryName));
    assertTrue(isCountryNotANeighbor);
  }

  @Test
  public void addBorder() {
    String country1 = "India";
    String country2 = "China";

    gameMap.addBorder(country1, country2);

    boolean isCountry2NeighborOfCountry1 = gameMap.getBorders().get(country1).contains(country2);
    assertTrue(isCountry2NeighborOfCountry1);

    boolean isCountry1NeighborOfCountry2 = gameMap.getBorders().get(country2).contains(country1);
    assertTrue(isCountry1NeighborOfCountry2);
  }

  @Test
  public void removeBorder() {
    String country1 = "India";
    String country2 = "China";

    gameMap.removeBorder(country1, country2);

    boolean isCountry2NeighborOfCountry1 = gameMap.getBorders().get(country1).contains(country2);
    assertFalse(isCountry2NeighborOfCountry1);

    boolean isCountry1NeighborOfCountry2 = gameMap.getBorders().get(country2).contains(country1);
    assertFalse(isCountry1NeighborOfCountry2);
  }

  @Test
  public void removeCountryBorders() {
    String countryName = "India";

    gameMap.removeCountryBorders(countryName);

    boolean isCountryRemovedInBorders = !gameMap.getBorders().containsKey(countryName);
    assertTrue(isCountryRemovedInBorders);

    boolean isCountryNotANeighbor =
        gameMap.getBorders().values().stream()
            .flatMap(Collection::stream)
            .map(gameMap.getCountries()::get)
            .map(Country::getName)
            .distinct()
            .noneMatch(country -> country.equals(countryName));
    assertTrue(isCountryNotANeighbor);
  }
}
