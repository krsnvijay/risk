import controllers.*;
import models.GameMapTest;
import models.PlayerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import utils.EditMapTest;
import utils.MapParserTest;

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
    EditMapTest.class,
    MapParserTest.class
})

public class RiskTestSuite {
}
