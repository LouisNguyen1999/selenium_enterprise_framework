package pages;

import config.ConfigLoader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.PopupHandler;

public class LoginPage extends BasePage {
    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(ConfigLoader.getInstance().getBaseUrl());
    }

    public void login(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
        // dismiss any unexpected modal that may appear after login
        PopupHandler.dismissIfPresent(driver);
    }

    public boolean isErrorVisible() {
        return isDisplayed(errorMessage);
    }
}
