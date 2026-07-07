package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.PopupHandler;

import java.time.Duration;

public class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected WebElement waitForVisible(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            // try to dismiss any overlay and wait a bit longer
            try {
                PopupHandler.dismissIfPresent(driver);
                PopupHandler.waitForNoOverlays(driver, Duration.ofSeconds(5));
            } catch (Exception ignored) {}
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            return longWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        }
    }

    protected WebElement waitForClickable(By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            try {
                PopupHandler.dismissIfPresent(driver);
                PopupHandler.waitForNoOverlays(driver, Duration.ofSeconds(5));
            } catch (Exception ignored) {}
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            return longWait.until(ExpectedConditions.elementToBeClickable(locator));
        }
    }

    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisible(locator).isDisplayed();
        } catch (NoSuchElementException | org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    protected void click(By locator) {
        PopupHandler.dismissIfPresent(driver);
        try {
            waitForClickable(locator).click();
        } catch (WebDriverException e) {
            PopupHandler.dismissIfPresent(driver);
            WebElement element = waitForVisible(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void type(By locator, String text) {
        PopupHandler.dismissIfPresent(driver);
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String textOf(By locator) {
        return waitForVisible(locator).getText();
    }
}
