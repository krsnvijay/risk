package utils.testsuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import utils.MapParserTest;
import utils.MapValidatorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({MapValidatorTest.class, MapParserTest.class})
public class UtilsTestSuite {}
