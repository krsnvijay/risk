package models.testsuites;

import models.GameMapTest;
import models.player.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite for models
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        GameMapTest.class,
        PlayerTest.class,
        PlayerRandomTest.class,
        PlayerBenevolentTest.class,
        PlayerCheaterTest.class,
        PlayerAggressiveTest.class
})

public class ModelsTestSuite {}
