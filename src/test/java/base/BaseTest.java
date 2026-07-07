package base;

import config.ConfigLoader;
import config.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;
import utils.PopupHandler;

public class BaseTest {
    protected WebDriver driver;
    protected LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        ConfigLoader config = ConfigLoader.getInstance();
        System.out.printf("Starting test on env=%s browser=%s headless=%s%n",
                config.getEnvironment(), config.getBrowserType(), config.isHeadless());
        DriverFactory.initializeDriver();
        driver = DriverFactory.getDriver();
        loginPage = new LoginPage(driver);
        driver.manage().window().maximize();
        loginPage.open();
        // dismiss any browser-level popup that appears immediately after open/login
        try {
            PopupHandler.dismissIfPresent(driver);
        } catch (Exception ignored) {}
    }

    public WebDriver getDriver() {
        return driver;
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
