package driver;

import org.openqa.selenium.WebDriver;

public class RemoteDriverManager extends DriverManager {
    @Override
    protected WebDriver createDriver() {
        throw new UnsupportedOperationException("Remote grid is not configured in this starter project.");
    }
}
