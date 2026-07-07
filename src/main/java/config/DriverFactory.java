package config;

import driver.DriverManager;
import driver.LocalDriverManager;
import org.openqa.selenium.WebDriver;

public final class DriverFactory {
    private static final ThreadLocal<DriverManager> DRIVER_MANAGER = new ThreadLocal<>();

    private DriverFactory() {}

    public static void initializeDriver() {
        DRIVER_MANAGER.set(new LocalDriverManager());
    }

    public static WebDriver getDriver() {
        if (DRIVER_MANAGER.get() == null) {
            initializeDriver();
        }
        return DRIVER_MANAGER.get().getDriver();
    }

    public static void quitDriver() {
        if (DRIVER_MANAGER.get() != null) {
            DRIVER_MANAGER.get().quitDriver();
            DRIVER_MANAGER.remove();
        }
    }
}
