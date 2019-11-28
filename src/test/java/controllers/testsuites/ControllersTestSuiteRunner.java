package controllers.testsuites;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Run the controllers test suite
 */
public class ControllersTestSuiteRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(ControllersTestSuite.class);

    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }

    System.out.println(result.wasSuccessful());
  }
}
