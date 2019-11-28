package utils.testsuites;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Test Suite Runner for Utils
 */
public class UtilsTestSuiteRunner {
  /**
   * Test Suite Runner for Utils
   * @param args for main
   */
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(UtilsTestSuite.class);

    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }

    System.out.println(result.wasSuccessful());
  }
}
