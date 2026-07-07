package components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class ProductCard extends BasePage {
    private final By nameLocator;
    private final By buttonLocator;

    public ProductCard(WebDriver driver, String productName) {
        super(driver);
        this.nameLocator = By.xpath("//div[contains(@class,'inventory_item_name') and text()='" + productName + "']");
        this.buttonLocator = By.xpath("//div[contains(@class,'inventory_item_name') and text()='" + productName + "']/ancestor::div[contains(@class,'inventory_item')]//button");
    }

    public boolean isVisible() {
        return isDisplayed(nameLocator);
    }

    public void addToCart() {
        click(buttonLocator);
    }
}
