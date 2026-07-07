package driver;

import config.BrowserFactory;
import config.ConfigLoader;
import org.openqa.selenium.WebDriver;

public class LocalDriverManager extends DriverManager {
    @Override
    protected WebDriver createDriver() {
        ConfigLoader config = ConfigLoader.getInstance();
        return BrowserFactory.createDriver(config.getBrowserType(), config.isHeadless());
    }
}
