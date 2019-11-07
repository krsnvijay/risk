package controllers.testsuites;

import controllers.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    BattleControllerTest.class,
    GameControllerTest.class,
    EditorControllerTest.class,
    GameRunnerTest.class,
    MainControllerTest.class,
    SetupControllerTest.class,
    StartUpControllerTest.class
})
public class ControllersTestSuite {
}
