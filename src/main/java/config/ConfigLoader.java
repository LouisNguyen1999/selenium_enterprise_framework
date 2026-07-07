package config;

import enums.BrowserType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final ConfigLoader INSTANCE = new ConfigLoader();
    private final Properties properties = new Properties();
    private final String environment;

    private ConfigLoader() {
        environment = System.getProperty("env", Constants.DEFAULT_ENV).trim().toLowerCase();
        String fileName = environment + ".properties";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config/" + fileName)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new IllegalStateException("Unable to locate config file: " + fileName);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load configuration", e);
        }
    }

    public static ConfigLoader getInstance() {
        return INSTANCE;
    }

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config property: " + key + " for environment: " + environment);
        }
        return value.trim();
    }

    public String getEnvironment() {
        return environment;
    }

    public String getBaseUrl() {
        return getProperty(Constants.BASE_URL);
    }

    public String getBrowser() {
        return getProperty(Constants.BROWSER);
    }

    public BrowserType getBrowserType() {
        return BrowserType.from(getBrowser());
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(getProperty(Constants.HEADLESS));
    }
}
