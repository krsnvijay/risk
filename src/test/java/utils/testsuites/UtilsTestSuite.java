package utils.testsuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import utils.EditMapTest;
import utils.MapParserTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({EditMapTest.class, MapParserTest.class})
public class UtilsTestSuite {
}
