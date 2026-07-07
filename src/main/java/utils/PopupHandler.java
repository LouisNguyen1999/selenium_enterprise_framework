package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public final class PopupHandler {
    private PopupHandler() {}

    private static final String[] CANDIDATE_XPATHS = new String[] {
        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'no thanks')]",
        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not now')]",
        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'later')]",
        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'maybe later')]",
        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'dismiss')]",
        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'close')]",
        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'skip')]",
        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'ok')]",
        "//div[@role='dialog']//button",
        "//div[contains(@class,'modal')]//button",
        "//div[contains(@class,'popup')]//button",
        "//button[@aria-label='close']",
        "//button[contains(@class,'close') or contains(@class,'Close')]",
    };

    private static final String[] OVERLAY_SELECTORS = new String[] {".modal", ".overlay", "[role='dialog']", ".popup"};

    private static final String[] TEXT_MATCHERS = new String[] {
        "change your password",
        "password manager",
        "password you just used was found",
        "change your saved password"
    };

    public static void dismissIfPresent(WebDriver driver) {
        if (driver == null) return;

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

        try {
            // 1) Try to find & click any candidate button quickly
            for (String xpath : CANDIDATE_XPATHS) {
                List<WebElement> els = driver.findElements(By.xpath(xpath));
                for (WebElement el : els) {
                    try {
                        if (!el.isDisplayed() || !el.isEnabled()) continue;
                        try {
                            el.click();
                        } catch (Exception e) {
                            try {
                                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                            } catch (Exception ignore) {}
                        }
                        // wait until overlays disappear
                        try {
                            shortWait.until((ExpectedCondition<Boolean>) d -> !overlaysPresent(d));
                        } catch (Exception ignored) {}
                        return;
                    } catch (Exception inner) {
                        // continue trying other candidates
                    }
                }
            }

            // 2) Send ESCAPE to dismiss typical modals
            try {
                new Actions(driver).sendKeys(Keys.ESCAPE).perform();
                try {
                    shortWait.until((ExpectedCondition<Boolean>) d -> !overlaysPresent(d));
                    return;
                } catch (Exception ignored) {}
            } catch (Exception ignore) {}

            // 2.b) Try to detect dialogs by visible text (e.g. Chrome password manager) and click OK-like buttons
            for (String text : TEXT_MATCHERS) {
                try {
                    String low = text.toLowerCase();
                    List<WebElement> matches = driver.findElements(By.xpath("//*[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + low + "')]") );
                    for (WebElement m : matches) {
                        try {
                            // find candidate buttons inside the dialog
                            List<WebElement> btns = m.findElements(By.xpath(".//button|.//a|.//input[@type='button']"));
                            for (WebElement b : btns) {
                                try {
                                    if (!b.isDisplayed() || !b.isEnabled()) continue;
                                    try { b.click(); } catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", b); }
                                    try { shortWait.until((ExpectedCondition<Boolean>) d -> !overlaysPresent(d)); } catch (Exception ignored) {}
                                    return;
                                } catch (Exception ignore) {}
                            }
                            // try OK by text
                            List<WebElement> ok = m.findElements(By.xpath(".//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'ok')]"));
                            for (WebElement b : ok) {
                                try {
                                    if (!b.isDisplayed() || !b.isEnabled()) continue;
                                    try { b.click(); } catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", b); }
                                    try { shortWait.until((ExpectedCondition<Boolean>) d -> !overlaysPresent(d)); } catch (Exception ignored) {}
                                    return;
                                } catch (Exception ignore) {}
                            }
                        } catch (Exception ignore) {}
                    }
                } catch (Exception ignore) {}
            }

            // 3) As last resort, hide overlay elements via JS and wait for them to disappear
            try {
                String script = "var s=['.modal','.overlay','[role=\\'dialog\\']','.popup']; s.forEach(function(sel){document.querySelectorAll(sel).forEach(function(e){e.style.display='none';});}); return true;";
                ((JavascriptExecutor) driver).executeScript(script);
                try {
                    shortWait.until((ExpectedCondition<Boolean>) d -> !overlaysPresent(d));
                    return;
                } catch (Exception ignored) {}
            } catch (Exception ignore) {}

            // 4) Aggressive: hide visible fixed/absolute elements with high z-index
            try {
                String aggressive = "Array.from(document.querySelectorAll('*')).forEach(function(el){var s=window.getComputedStyle(el); if((s.position==='fixed' || s.position==='absolute') && el.offsetWidth>0 && el.offsetHeight>0){var z=parseInt(s.zIndex)||0; if(z>100){el.style.display='none';}}}); return true;";
                ((JavascriptExecutor) driver).executeScript(aggressive);
                try {
                    shortWait.until((ExpectedCondition<Boolean>) d -> !overlaysPresent(d));
                } catch (Exception ignored) {}
            } catch (Exception ignore) {}

        } catch (Exception e) {
            // swallow - this helper must never break tests
        }
    }

    private static boolean overlaysPresent(WebDriver driver) {
        try {
            String script = "var selectors=['.modal','.overlay','[role=\\'dialog\\']','.popup']; for(var i=0;i<selectors.length;i++){var els=document.querySelectorAll(selectors[i]); for(var j=0;j<els.length;j++){var e=els[j]; var s=window.getComputedStyle(e); if(s.display!== 'none' && s.visibility!== 'hidden' && e.offsetWidth>0 && e.offsetHeight>0) return true;}} return false;";
            Object res = ((JavascriptExecutor) driver).executeScript(script);
            if (res instanceof Boolean) return (Boolean) res;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean waitForNoOverlays(WebDriver driver, java.time.Duration timeout) {
        if (driver == null) return true;
        try {
            new WebDriverWait(driver, timeout).until((ExpectedCondition<Boolean>) d -> !overlaysPresent(d));
            return true;
        } catch (Exception e) {
            return !overlaysPresent(driver);
        }
    }
}
