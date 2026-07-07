package config;

import enums.BrowserType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BrowserFactory {
    private BrowserFactory() {}

    public static WebDriver createDriver(BrowserType browser, boolean headless) {
        Objects.requireNonNull(browser, "browser must not be null");
        return switch (browser) {
            case FIREFOX -> {
                WebDriverManager.firefoxdriver().setup();
                yield new FirefoxDriver();
            }
            case EDGE -> {
                WebDriverManager.edgedriver().setup();
                yield new EdgeDriver();
            }
            case CHROME -> createChromeDriver(headless);
        };
    }

    public static WebDriver createDriver(String browser, boolean headless) {
        return createDriver(BrowserType.from(browser), headless);
    }

    private static ChromeDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("password_manager_enabled", false);
        prefs.put("browser.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("password_leak_detection_enabled", false);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-features=PasswordManagerOnboarding,PasswordLeakDetection,PasswordGeneration,AutofillServerCommunication,TranslateUI");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-blink-features=AutofillCredentials");
        options.addArguments("--start-maximized");
        options.addArguments("--guest");

        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        }

        ChromeDriver driver = new ChromeDriver(options);
        try {
            String script = "(function(){var matcher=/change your password|password you just used was found|password manager/i;function removeMatches(node){try{if(!node) return; if(node.nodeType===3){ if(matcher.test(node.textContent)){var el=node.parentElement; while(el && el!==document.body){ if(el.getAttribute && (el.getAttribute('role')==='dialog' || el.classList && (el.classList.contains('modal')||el.classList.contains('popup')))){el.style.display='none'; return;} el=el.parentElement;} }} else if(node.nodeType===1){ if(matcher.test(node.innerText || '')){ node.style.display='none'; } }}catch(e){} } var obs=new MutationObserver(function(mutations){mutations.forEach(function(m){ m.addedNodes.forEach(function(n){ removeMatches(n); }); removeMatches(m.target); });}); obs.observe(document,{childList:true,subtree:true}); removeMatches(document.body);})();";
            Map<String, Object> cmd = new HashMap<>();
            cmd.put("source", script);
            driver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", cmd);
        } catch (Exception ignored) {
            // CDP injection is best-effort; normal Selenium interactions still work without it.
        }

        return driver;
    }
}
