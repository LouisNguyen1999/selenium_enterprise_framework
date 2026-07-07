package tests.login;

import base.BaseTest;
import dataprovider.UserDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;

public class LoginTest extends BaseTest {
    @Test(dataProvider = "loginUsers", dataProviderClass = UserDataProvider.class)
    public void userCanLogin(String username, String password) {
        loginPage.login(username, password);
        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isInventoryVisible(), "Inventory page should be visible after login");
    }
}
