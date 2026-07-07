package hooks;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BeforeAfterMethod {
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        System.out.println("Starting test execution");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        System.out.println("Finishing test execution");
    }
}
