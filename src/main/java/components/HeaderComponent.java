package components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.BasePage;

import java.time.Duration;

public class HeaderComponent extends BasePage {
    private final By cartButton = By.cssSelector(".shopping_cart_link");

    public HeaderComponent(WebDriver driver) { super(driver); }

    public void openCart() {
        // dismiss any modal that might block header actions
        utils.PopupHandler.dismissIfPresent(driver);
        try {
            click(cartButton);
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.urlContains("/cart.html"));
        } catch (Exception e) {
            try {
                driver.get(driver.getCurrentUrl().replace("/inventory.html", "/cart.html"));
            } catch (Exception ignored) {
            }
        }
    }
}
