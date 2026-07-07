package enums;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Browser value must not be blank. Supported browsers: " + supportedValues());
        }

        try {
            return BrowserType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported browser: " + value + ". Supported browsers: " + supportedValues(), e);
        }
    }

    private static String supportedValues() {
        return Arrays.stream(values())
                .map(browser -> browser.name().toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(", "));
    }
}
