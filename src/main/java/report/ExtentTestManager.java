package report;

import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    public static ExtentTest getTest() {
        return TEST.get();
    }

    public static void setTest(ExtentTest test) {
        TEST.set(test);
    }

    public static void removeTest() {
        TEST.remove();
    }
}
