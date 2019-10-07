import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import models.Continent;
import models.Country;
import models.GameMap;
import utils.MapParser;

public class PlaceholderTest {

  GameMap testMap;

  @Before
  public void setUp() throws Exception {
    ArrayList<String> testSection = new ArrayList<String>();
    Map<String, Set<String>> testBorder = new HashMap<String, Set<String>>();
    Map<String, Continent> testContinent = new HashMap<String, Continent>();
    Map<String, Country> testCountry = new HashMap<String, Country>();
    String testFile = "C:\\Users\\Siddharth Singh\\Desktop";
    Continent tContinent = new Continent("Asia", 3);
    Country tCountry = new Country("India", "Asia");
    testSection.add("dssd");
    testBorder.put("India", Arrays.asList("2", "3").stream().collect(Collectors.toSet()));
    testContinent.put("Asia", tContinent);
    testCountry.put("India", tCountry);
    testMap = new GameMap(testSection, testBorder, testContinent, testCountry, testFile);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() throws Exception {
    assertEquals(testMap, MapParser.loadMap("C:\\Users\\Siddharth Singh\\Desktop\\ameroki.map"));
    fail("Not yet implemented");
  }
}
