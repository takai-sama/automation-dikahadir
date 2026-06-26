package com.kelompok1.cucumber.pages.mobile;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.kelompok1.cucumber.core.ConfigReader;
import com.kelompok1.cucumber.pages.BasePage;

/**
 * Page Object for the Mobile Mirroring Login page.
 * URL: https://magang.dikahadir.com/absen/login
 *
 * Locators here target the mobile mirroring panel.
 * Update these if the mobile UI differs from the web admin panel.
 */
public class LoginMobilePage extends BasePage {

    private final By emailField    = By.id("email");
    private final By passwordField = By.id("password");
    private final By loginButton   = By.cssSelector("button[type='submit']");
    private final By errorAlert    = By.cssSelector(".MuiAlert-filledError");

    public LoginMobilePage() {
        super();
    }

    public LoginMobilePage navigateToLoginPage() {
        navigateTo(ConfigReader.getProperty("base.url"));
        return this;
    }

    public LoginMobilePage enterEmail(String email) {
        typeText(emailField, email);
        return this;
    }

    public LoginMobilePage enterPassword(String password) {
        typeText(passwordField, password);
        return this;
    }

    public LoginMobilePage clickLogin() {
        click(loginButton);
        return this;
    }

    public LoginMobilePage doLogin(String email, String password) {
        logger.info("Mobile login with email: {}", email);
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
