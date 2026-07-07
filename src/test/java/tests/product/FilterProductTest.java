package tests.product;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductPage;

public class FilterProductTest extends BaseTest {
    @Test
    public void userCanFilterProductsByPrice() {
        loginPage.login("standard_user", "secret_sauce");
        ProductPage productPage = new ProductPage(driver);
        productPage.filterBy("Price (low to high)");
        Assert.assertTrue(driver.getPageSource().contains("Sauce Labs Bike Light"));
    }
}
