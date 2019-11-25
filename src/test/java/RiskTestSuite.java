import controllers.*;
import models.GameMapTest;
import models.player.PlayerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import utils.MapParserTest;
import utils.MapValidatorTest;

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
  MapParserTest.class
})
public class RiskTestSuite {}
