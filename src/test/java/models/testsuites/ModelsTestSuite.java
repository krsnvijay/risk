package models.testsuites;

import models.GameMapTest;
import models.player.PlayerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({GameMapTest.class, PlayerTest.class})
public class ModelsTestSuite {}
