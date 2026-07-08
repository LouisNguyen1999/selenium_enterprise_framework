package utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class PropertiesUtils {
    private PropertiesUtils() {}

    public static Properties load(String filePath) {
        Path path = toPath(filePath);
        try (InputStream inputStream = Files.newInputStream(path)) {
            return load(inputStream, filePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load properties file: " + filePath, e);
        }
    }

    public static Properties loadFromResource(String resourcePath) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("Properties resource not found: " + resourcePath);
        }
        try (inputStream) {
            return load(inputStream, resourcePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to close properties resource: " + resourcePath, e);
        }
    }

    public static Properties loadEnvironmentConfig(String environment) {
        if (environment == null || environment.isBlank()) {
            throw new IllegalArgumentException("Environment must not be null or blank");
        }
        return loadFromResource("config/" + environment.trim().toLowerCase() + ".properties");
    }

    public static String getValue(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value.trim();
    }

    public static String getValue(Properties properties, String key, String defaultValue) {
        String value = properties.getProperty(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private static Properties load(InputStream inputStream, String source) {
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load properties from: " + source, e);
        }
    }

    private static Path toPath(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Properties file path must not be null or blank");
        }
        return Paths.get(filePath);
    }
}
