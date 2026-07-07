package dataprovider;

import org.testng.annotations.DataProvider;

public class UserDataProvider {
    @DataProvider(name = "loginUsers")
    public Object[][] loginUsers() {
        return new Object[][]{{"standard_user", "secret_sauce"}};
    }
}
