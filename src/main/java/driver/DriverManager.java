package driver;

import org.openqa.selenium.WebDriver;

public abstract class DriverManager {
    protected ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();

    protected abstract WebDriver createDriver();

    public WebDriver getDriver() {
        if (webDriver.get() == null) {
            webDriver.set(createDriver());
        }
        return webDriver.get();
    }

    public void quitDriver() {
        if (webDriver.get() != null) {
            webDriver.get().quit();
            webDriver.remove();
        }
    }
}
