package tests.product;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductPage;

public class SearchProductTest extends BaseTest {
    @Test
    public void userCanSeeInventoryList() {
        loginPage.login("standard_user", "secret_sauce");
        ProductPage productPage = new ProductPage(driver);
        productPage.searchProduct("Backpack");
        Assert.assertTrue(driver.getPageSource().contains("Sauce Labs Backpack"));
    }
}
