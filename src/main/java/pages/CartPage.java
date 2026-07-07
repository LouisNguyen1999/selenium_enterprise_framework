package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.PopupHandler;

import java.time.Duration;

public class CartPage extends BasePage {
    private final By checkoutButton = By.cssSelector("[data-test='checkout']");
    private final By firstNameInput = By.id("first-name");

    public CartPage(WebDriver driver) { super(driver); }

    public void open() {
        driver.get(driver.getCurrentUrl().replace("/inventory.html", "/cart.html"));
    }

    public void checkout() {
        PopupHandler.dismissIfPresent(driver);

        String cartUrl = driver.getCurrentUrl();
        try {
            WebElement checkoutBtn = waitForClickable(checkoutButton);
            checkoutBtn.click();
        } catch (Exception e) {
            navigateToCheckoutStepOne(cartUrl);
        }

        waitForCheckoutStepOne(cartUrl);
    }


    private void navigateToCheckoutStepOne(String currentUrl) {
        String targetUrl;
        if (currentUrl.contains("inventory.html")) {
            targetUrl = currentUrl.replace("inventory.html", "checkout-step-one.html");
        } else if (currentUrl.contains("cart.html")) {
            targetUrl = currentUrl.replace("cart.html", "checkout-step-one.html");
        } else {
            int lastSlash = currentUrl.lastIndexOf("/");
            targetUrl = currentUrl.substring(0, lastSlash + 1) + "checkout-step-one.html";
        }

        driver.get(targetUrl);
    }

    private void waitForCheckoutStepOne(String fallbackUrl) {
        WebDriverWait checkoutWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            checkoutWait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));
        } catch (Exception firstAttempt) {
            navigateToCheckoutStepOne(driver.getCurrentUrl().contains("cart.html") ? driver.getCurrentUrl() : fallbackUrl);
            checkoutWait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));
        }
    }
}
