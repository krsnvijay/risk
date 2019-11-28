import controllers.*;
import models.GameMapTest;
import models.player.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import utils.ConquestMapParserTest;
import utils.DominationMapParserTest;
import utils.GamePersistenceHandlerTest;
import utils.MapValidatorTest;

/** Test Suite Runner for Risk */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  BattleControllerTest.class,
  EditorControllerTest.class,
  GameControllerTest.class,
  RunnerTest.class,
  MainControllerTest.class,
  StartUpControllerTest.class,
  SetupControllerTest.class,
  PlayerAggressiveTest.class,
  PlayerTest.class,
  PlayerCheaterTest.class,
  PlayerRandomTest.class,
  PlayerBenevolentTest.class,
  MapValidatorTest.class,
  GameMapTest.class,
  ConquestMapParserTest.class,
  GamePersistenceHandlerTest.class,
  DominationMapParserTest.class
})
public class RiskTestSuite {}
