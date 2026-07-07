package listeners;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import report.ExtentManager;
import report.ExtentTestManager;
import utils.ScreenshotUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class TestListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        ExtentTestManager.setTest(ExtentManager.getInstance()
                .createTest(result.getTestClass().getName() + "." + result.getMethod().getMethodName()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.PASS, "Test passed");
        }
        ExtentTestManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test failed: " + result.getName());
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().fail(result.getThrowable());
            attachFailureArtifacts(result);
        }
        ExtentTestManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.SKIP, "Test skipped");
            if (result.getThrowable() != null) {
                ExtentTestManager.getTest().skip(result.getThrowable());
            }
        }
        ExtentTestManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }

    private void attachFailureArtifacts(ITestResult result) {
        WebDriver driver = getDriver(result);
        if (driver == null) {
            ExtentTestManager.getTest().warning("No WebDriver instance available for failure artifacts");
            return;
        }

        try {
            String screenshotPath = ScreenshotUtils.capture(driver, result.getMethod().getMethodName());
            ExtentTestManager.getTest().addScreenCaptureFromPath(screenshotPath);
            ExtentTestManager.getTest().log(Status.INFO, "Screenshot saved: " + screenshotPath);
        } catch (Exception e) {
            ExtentTestManager.getTest().warning("Unable to capture screenshot: " + e.getMessage());
        }

        try {
            Path htmlPath = savePageSource(driver, result.getMethod().getMethodName());
            ExtentTestManager.getTest().log(Status.INFO, "Page source saved: " + htmlPath);
        } catch (Exception e) {
            ExtentTestManager.getTest().warning("Unable to save page source: " + e.getMessage());
        }
    }

    private WebDriver getDriver(ITestResult result) {
        Object instance = result.getInstance();
        if (instance == null) {
            return null;
        }

        try {
            Object driver = instance.getClass().getMethod("getDriver").invoke(instance);
            if (driver instanceof WebDriver webDriver) {
                return webDriver;
            }
        } catch (Exception ignored) {
            // Test classes that do not expose getDriver simply do not produce browser artifacts.
        }
        return null;
    }

    private Path savePageSource(WebDriver driver, String testName) throws Exception {
        Path target = Path.of("screenshots", testName + "_" + System.currentTimeMillis() + ".html");
        Files.createDirectories(target.getParent());
        Files.writeString(target, driver.getPageSource());
        return target;
    }
}
