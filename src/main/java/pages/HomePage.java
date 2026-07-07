package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private final By inventoryContainer = By.cssSelector(".inventory_list");
    private final By title = By.cssSelector(".title");

    public HomePage(WebDriver driver) { super(driver); }

    public boolean isInventoryVisible() {
        return isDisplayed(inventoryContainer);
    }

    public String getTitle() {
        return textOf(title);
    }
}
