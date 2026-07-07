package tests.cart;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductPage;

public class AddCartTest extends BaseTest {
    @Test
    public void userCanAddFirstProductToCart() {
        loginPage.login("standard_user", "secret_sauce");
        ProductPage productPage = new ProductPage(driver);
        productPage.addFirstProductToCart();
        Assert.assertTrue(productPage.isCartBadgeVisible(), "Cart badge should appear after adding a product");
    }
}
