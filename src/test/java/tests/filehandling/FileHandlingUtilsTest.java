package tests.filehandling;

import dataprovider.UserDataProvider;
import models.User;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.CsvUtils;
import utils.ExcelUtils;
import utils.FileUtils;
import utils.JsonUtils;
import utils.PropertiesUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FileHandlingUtilsTest {
    @Test
    public void shouldReadEnvironmentConfigFromProperties() {
        Properties properties = PropertiesUtils.loadEnvironmentConfig("qa");

        Assert.assertEquals(PropertiesUtils.getValue(properties, "browser"), "chrome");
        Assert.assertEquals(PropertiesUtils.getValue(properties, "headless", "false"), "true");
        Assert.assertEquals(PropertiesUtils.getValue(properties, "app.name"), "Sauce Demo");
    }

    @Test
    public void shouldReadLoginUserFromJson() {
        Path loginJson = FileUtils.getResourcePath("testdata/login.json");
        User user = JsonUtils.readJson(loginJson.toString(), User.class);

        Assert.assertEquals(user.getUsername(), "standard_user");
        Assert.assertEquals(user.getPassword(), "secret_sauce");
        Assert.assertTrue(JsonUtils.prettyPrint(user).contains("standard_user"));
    }

    @Test
    public void shouldReadUsersFromCsv() {
        Path usersCsv = FileUtils.getResourcePath("testdata/users.csv");
        List<Map<String, String>> users = CsvUtils.readCsvAsMaps(usersCsv.toString());

        Assert.assertEquals(users.size(), 3);
        Assert.assertEquals(users.get(0).get("username"), "standard_user");
        Assert.assertEquals(users.get(1).get("active"), "false");
    }

    @Test(dataProvider = "excelUsers", dataProviderClass = UserDataProvider.class)
    public void shouldUseExcelRowsFromDataProvider(String username, String password, String role, String active) {
        Assert.assertFalse(username.isBlank());
        Assert.assertEquals(password, "secret_sauce");
        Assert.assertEquals(role, "customer");
        Assert.assertTrue(List.of("true", "false").contains(active));
    }

    @Test
    public void shouldReadExcelUsersAsMaps() {
        Path usersExcel = FileUtils.getResourcePath("testdata/users.xlsx");
        List<Map<String, String>> users = ExcelUtils.readSheetAsMaps(usersExcel.toString(), "Users");

        Assert.assertEquals(users.size(), 3);
        Assert.assertEquals(users.get(2).get("username"), "performance_glitch_user");
    }
}
