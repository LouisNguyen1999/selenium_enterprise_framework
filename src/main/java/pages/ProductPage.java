package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.PopupHandler;

public class ProductPage extends BasePage {
    private final By sortDropdown = By.cssSelector(".product_sort_container");
    private final By addToCartButton = By.cssSelector("button.btn_inventory");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");

    public ProductPage(WebDriver driver) { super(driver); }

    public void searchProduct(String productName) {
        WebElement element = waitForVisible(By.cssSelector(".inventory_list"));
        if (!element.getText().toLowerCase().contains(productName.toLowerCase())) {
            throw new IllegalStateException("Product not found: " + productName);
        }
    }

    public void filterBy(String option) {
        click(sortDropdown);
        driver.findElement(By.xpath("//option[.='" + option + "']")).click();
    }

    public void addFirstProductToCart() {
        click(addToCartButton);
        // dismiss potential popups that may appear when adding to cart
        PopupHandler.dismissIfPresent(driver);
    }

    public boolean isCartBadgeVisible() {
        return isDisplayed(cartBadge);
    }
}
