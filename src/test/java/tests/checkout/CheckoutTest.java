package tests.checkout;

import base.BaseTest;
import components.HeaderComponent;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.ProductPage;

public class CheckoutTest extends BaseTest {
    @Test
    public void userCanCompleteCheckout() {
        loginPage.login("standard_user", "secret_sauce");
        ProductPage productPage = new ProductPage(driver);
        productPage.addFirstProductToCart();
        HeaderComponent header = new HeaderComponent(driver);
        header.openCart();
        CartPage cartPage = new CartPage(driver);
        cartPage.checkout();
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillCustomerInfo("Ada", "Lovelace", "12345");
        checkoutPage.finish();
        Assert.assertTrue(checkoutPage.isComplete(), "Checkout completion page should appear");
    }
}
