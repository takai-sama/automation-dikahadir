package com.kelompok1.cucumber.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kelompok1.cucumber.core.ConfigReader;
import com.kelompok1.cucumber.core.DriverManager;

import java.time.Duration;
import java.util.List;

/**
 * Base Page Object with reusable Selenium methods.
 * Shared by both web and mobile page objects.
 * All page classes should extend this class.
 */
public abstract class BasePage {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final WebDriverWait shortWait;

    public BasePage() {
        this.driver    = DriverManager.getDriver();
        this.wait      = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("standard.wait", 15)));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("short.wait", 5)));
    }

    protected void navigateTo(String url) {
        driver.get(url);
        logger.info("Navigated to: {}", url);
    }

    protected void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
        logger.debug("Clicked element: {}", locator);
    }

    protected void typeText(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
        logger.debug("Typed '{}' into element: {}", text, locator);
    }

    protected String getText(By locator) {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
        } catch (Exception e) {
            logger.warn("Element not found for getText: {}", locator);
            return "";
        }
    }

    protected boolean isDisplayed(By locator) {
        try {
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    protected WebElement findElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void waitForVisibility(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected String getPageTitle() {
        return driver.getTitle();
    }

    protected boolean waitForUrlToBe(String expectedUrl) {
        try {
            return wait.until(ExpectedConditions.urlToBe(expectedUrl));
        } catch (Exception e) {
            logger.warn("URL did not become '{}'. Current: {}", expectedUrl, getCurrentUrl());
            return false;
        }
    }

    protected boolean waitForUrlContains(String partialUrl) {
        try {
            return wait.until(ExpectedConditions.urlContains(partialUrl));
        } catch (Exception e) {
            logger.warn("URL did not contain '{}'. Current: {}", partialUrl, getCurrentUrl());
            return false;
        }
    }

    protected boolean waitForElementPresent(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
