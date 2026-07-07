package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    public static String capture(WebDriver driver, String name) {
        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path target = Path.of("screenshots", name + "_" + timestamp + ".png");
        try {
            Files.createDirectories(target.getParent());
            Files.copy(file.toPath(), target);
            return target.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save screenshot", e);
        }
    }
}
