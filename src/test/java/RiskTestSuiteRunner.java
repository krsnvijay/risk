import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/** Test Suite Runner for Risk */
public class RiskTestSuiteRunner {
  /**
   * Main function to run the suite
   *
   * @param args param args
   */
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(RiskTestSuite.class);

    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }

    System.out.println(result.wasSuccessful());
  }
}
