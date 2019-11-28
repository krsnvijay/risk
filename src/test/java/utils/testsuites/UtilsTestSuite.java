package utils.testsuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import utils.ConquestMapParserTest;
import utils.DominationMapParserTest;
import utils.MapValidatorTest;

/**
 * Test suite for utils
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({MapValidatorTest.class, DominationMapParserTest.class, ConquestMapParserTest.class})
public class UtilsTestSuite {}
