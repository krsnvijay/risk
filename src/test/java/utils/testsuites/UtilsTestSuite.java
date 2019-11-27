package utils.testsuites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import utils.ConquestMapParser;
import utils.DominationMapParserTest;
import utils.MapValidatorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({MapValidatorTest.class, DominationMapParserTest.class, ConquestMapParser.class})
public class UtilsTestSuite {}
