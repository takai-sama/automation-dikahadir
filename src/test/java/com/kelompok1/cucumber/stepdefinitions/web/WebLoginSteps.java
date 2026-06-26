package com.kelompok1.cucumber.stepdefinitions.web;

import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.kelompok1.cucumber.core.TestData;
import com.kelompok1.cucumber.pages.web.LoginWebPage;

/**
 * Step Definitions for Web Admin Login feature.
 * URL: https://magang.dikahadir.com/authentication/login
 *
 * LoginWebPage is created lazily inside each step method (not in the constructor)
 * to avoid NPE — BasePage reads the WebDriver which isn't set until @Before fires.
 */
public class WebLoginSteps {

    private static final Logger logger = LoggerFactory.getLogger(WebLoginSteps.class);

    private LoginWebPage loginPage() {
        return new LoginWebPage();
    }

    // =========================================================================
    // GIVEN
    // =========================================================================

    @Given("user is on the web login page")
    public void userIsOnWebLoginPage() {
        loginPage().navigateToLoginPage();
        Assert.assertTrue(loginPage().isLoginPageDisplayed(),
            "Web login page should be displayed — check base.url in config-web.properties");
    }

    @Given("web user is logged in")
    public void webUserIsLoggedIn() {
        logger.info("Setting up authenticated web session...");
        loginPage().navigateToLoginPage();
        loginPage().doLogin(TestData.validEmail(), TestData.validPassword());
        Assert.assertTrue(loginPage().isLoginSuccessful(),
            "Precondition failed: web login unsuccessful. Check test-data-web.properties");
    }

    // =========================================================================
    // WHEN
    // =========================================================================

    @When("user logs in with valid web credentials")
    public void userLogsInWithValidWebCredentials() {
        logger.info("Logging in to web panel with valid credentials");
        loginPage().doLogin(TestData.validEmail(), TestData.validPassword());
    }

    @When("user logs in to web with email {string} and password {string}")
    public void userLogsInToWebWithEmailAndPassword(String email, String password) {
        logger.info("Attempting web login with email: '{}'", email);
        loginPage().doLogin(email, password);
    }

    @When("user enters web email {string} and password {string} without submitting")
    public void userEntersWebEmailWithoutSubmitting(String email, String password) {
        logger.info("Entering web email '{}' to trigger browser validation", email);
        loginPage().enterEmail(email).enterPassword(password).clickLogin();
    }

    // =========================================================================
    // THEN
    // =========================================================================

    @Then("web user should be redirected to the dashboard")
    public void webUserRedirectedToDashboard() {
        Assert.assertTrue(loginPage().isLoginSuccessful(),
            "Expected redirect to web dashboard. Check dashboard.url in config-web.properties");
    }

    @Then("web error message should be displayed")
    public void webErrorMessageDisplayed() {
        Assert.assertTrue(loginPage().isErrorDisplayed(),
            "Expected MUI error alert on web login page but it was not found");
    }

    @Then("web error message should contain {string}")
    public void webErrorMessageContains(String expectedError) {
        String actualError = loginPage().getErrorMessage();
        Assert.assertTrue(actualError.contains(expectedError),
            String.format("Expected web error to contain '%s' but got: '%s'", expectedError, actualError));
    }

    @Then("web browser validation should contain {string}")
    public void webBrowserValidationContains(String expected) {
        String actual = loginPage().getEmailValidationMessage();
        Assert.assertTrue(actual.contains(expected),
            String.format("Expected web browser validation to contain '%s' but got: '%s'", expected, actual));
    }

    @Then("web user should remain on the login page")
    public void webUserRemainsOnLoginPage() {
        Assert.assertTrue(loginPage().isLoginPageDisplayed(),
            "Web user should still be on the login page after a failed attempt");
    }
}
