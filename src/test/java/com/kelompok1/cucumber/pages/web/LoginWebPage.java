package com.kelompok1.cucumber.pages.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.kelompok1.cucumber.core.ConfigReader;
import com.kelompok1.cucumber.pages.BasePage;

/**
 * Page Object for the Web Admin Login page.
 * URL: https://magang.dikahadir.com/authentication/login
 *
 * Locators here are specific to the web admin panel.
 * If the mobile mirroring panel has different locators, use LoginMobilePage instead.
 */
public class LoginWebPage extends BasePage {

    private final By emailField    = By.id("email");
    private final By passwordField = By.id("password");
    private final By loginButton   = By.cssSelector("button[type='submit']");
    private final By errorAlert    = By.cssSelector(".MuiAlert-filledError");

    public LoginWebPage() {
        super();
    }

    public LoginWebPage navigateToLoginPage() {
        navigateTo(ConfigReader.getProperty("base.url"));
        return this;
    }

    public LoginWebPage enterEmail(String email) {
        typeText(emailField, email);
        return this;
    }

    public LoginWebPage enterPassword(String password) {
        typeText(passwordField, password);
        return this;
    }

    public LoginWebPage clickLogin() {
        click(loginButton);
        return this;
    }

    public LoginWebPage doLogin(String email, String password) {
        logger.info("Web login with email: {}", email);
        enterEmail(email).enterPassword(password).clickLogin();
        return this;
    }

    public boolean isLoginSuccessful() {
        String dashboardUrl = ConfigReader.getProperty("dashboard.url");
        return waitForUrlToBe(dashboardUrl);
    }

    public boolean isLoginPageDisplayed() {
        return isDisplayed(emailField) && isDisplayed(passwordField) && isDisplayed(loginButton);
    }

    public String getErrorMessage() {
        return getText(errorAlert);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorAlert);
    }

    public String getEmailValidationMessage() {
        waitForElementPresent(emailField);
        WebElement emailInput = driver.findElement(emailField);
        String message = emailInput.getAttribute("validationMessage");
        logger.info("Browser validation message: '{}'", message);
        return message;
    }
}
