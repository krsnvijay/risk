import controllers.*;
import models.GameMapTest;
import models.player.PlayerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import utils.DominationMapParserTest;
import utils.MapValidatorTest;

/**
 * Test Suite for risk
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    BattleControllerTest.class,
    EditorControllerTest.class,
    GameControllerTest.class,
    GameRunnerTest.class,
    MainControllerTest.class,
    StartUpControllerTest.class,
    SetupControllerTest.class,
    GameMapTest.class,
    PlayerTest.class,
    MapValidatorTest.class,
    DominationMapParserTest.class
})
public class RiskTestSuite {}
