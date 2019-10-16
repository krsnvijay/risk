package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import models.Continent;
import models.Country;
import models.GameMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlaceholderTest {

  GameMap testMap;

  @Before
  public void setUp() throws Exception {
    ArrayList<String> testSection = new ArrayList<>();
    Map<String, Set<String>> testBorder = new HashMap<>();
    Map<String, Continent> testContinent = new HashMap<>();
    Map<String, Country> testCountry = new HashMap<>();
    String testFile = "C:\\Users\\Siddharth Singh\\Desktop";
    Continent tContinent = new Continent("Orange", "Asia", 3);
    Country tCountry = new Country("India", "Asia");
    testSection.add(
        "1 siberia 1 329 152\r\n"
            + "2 worrick 1 308 199\r\n"
            + "3 yazteck 1 284 260\r\n"
            + "4 kongrolo 1 278 295");
    testBorder.put("siberia", new HashSet<>(Arrays.asList("worrick", "yazteck")));
    testContinent.put("Asia", tContinent);
    testCountry.put("India", tCountry);
    testMap = new GameMap(testSection, testBorder, testContinent, testCountry, testFile);
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void test() throws Exception {
    assertEquals(testMap, MapParser.loadMap("C:\\Users\\Siddharth Singh\\Desktop\\test.map"));
    fail("Not yet implemented");
  }
}
