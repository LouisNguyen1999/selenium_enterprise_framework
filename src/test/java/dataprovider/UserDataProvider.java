package dataprovider;

import org.testng.annotations.DataProvider;
import utils.ExcelUtils;
import utils.FileUtils;

public class UserDataProvider {
    @DataProvider(name = "loginUsers")
    public Object[][] loginUsers() {
        return new Object[][]{{"standard_user", "secret_sauce"}};
    }

    @DataProvider(name = "excelUsers")
    public Object[][] excelUsers() {
        String usersExcel = FileUtils.getResourcePath("testdata/users.xlsx").toString();
        return ExcelUtils.readSheetWithoutHeader(usersExcel, "Users");
    }
}
