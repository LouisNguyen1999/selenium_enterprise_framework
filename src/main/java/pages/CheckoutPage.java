package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.PopupHandler;

import java.time.Duration;

public class CheckoutPage extends BasePage {
    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");
    private final By postalCode = By.id("postal-code");
    private final By continueButton = By.cssSelector("[data-test='continue']");
    private final By finishButton = By.cssSelector("[data-test='finish']");
    private final By completeHeader = By.cssSelector(".complete-header");

    public CheckoutPage(WebDriver driver) { super(driver); }

    public void fillCustomerInfo(String first, String last, String zip) {
        ensureCheckoutStepOneLoaded();
        PopupHandler.dismissIfPresent(driver);
        type(firstName, first);
        type(lastName, last);
        type(postalCode, zip);
        click(continueButton);
        PopupHandler.dismissIfPresent(driver);
    }

    private void ensureCheckoutStepOneLoaded() {
        WebDriverWait checkoutWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            checkoutWait.until(ExpectedConditions.visibilityOfElementLocated(firstName));
        } catch (Exception firstAttempt) {
            String url = driver.getCurrentUrl();
            if (url.contains("/cart")) {
                driver.get(url.replace("/cart.html", "/checkout-step-one.html"));
            } else if (url.contains("/inventory")) {
                driver.get(url.replace("/inventory.html", "/checkout-step-one.html"));
            } else if (!url.contains("/checkout-step-one")) {
                int lastSlash = url.lastIndexOf("/");
                driver.get(url.substring(0, lastSlash + 1) + "checkout-step-one.html");
            }
            checkoutWait.until(ExpectedConditions.visibilityOfElementLocated(firstName));
        }
    }

    public void finish() {
        PopupHandler.dismissIfPresent(driver);
        click(finishButton);
    }

    public boolean isComplete() {
        return isDisplayed(completeHeader);
    }
}
