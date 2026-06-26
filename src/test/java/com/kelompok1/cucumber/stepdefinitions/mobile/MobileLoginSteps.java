package com.kelompok1.cucumber.stepdefinitions.mobile;

import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.kelompok1.cucumber.core.TestData;
import com.kelompok1.cucumber.pages.mobile.LoginMobilePage;

/**
 * Step Definitions for Mobile Mirroring Login feature.
 * URL: https://magang.dikahadir.com/absen/login
 *
 * LoginMobilePage is created lazily inside each step method (not in the constructor)
 * to avoid NPE — BasePage reads the WebDriver which isn't set until @Before fires.
 */
public class MobileLoginSteps {

    private static final Logger logger = LoggerFactory.getLogger(MobileLoginSteps.class);

    private LoginMobilePage loginPage() {
        return new LoginMobilePage();
    }

    // =========================================================================
    // GIVEN
    // =========================================================================

    @Given("user is on the mobile login page")
    public void userIsOnMobileLoginPage() {
        loginPage().navigateToLoginPage();
        Assert.assertTrue(loginPage().isLoginPageDisplayed(),
            "Mobile login page should be displayed — check base.url in config-mobile.properties");
    }

    @Given("mobile user is logged in")
    public void mobileUserIsLoggedIn() {
        logger.info("Setting up authenticated mobile session...");
        loginPage().navigateToLoginPage();
        loginPage().doLogin(TestData.validEmail(), TestData.validPassword());
        Assert.assertTrue(loginPage().isLoginSuccessful(),
            "Precondition failed: mobile login unsuccessful. Check test-data-mobile.properties");
    }

    // =========================================================================
    // WHEN
    // =========================================================================

    @When("user logs in with valid mobile credentials")
    public void userLogsInWithValidMobileCredentials() {
        logger.info("Logging in to mobile panel with valid credentials");
        loginPage().doLogin(TestData.validEmail(), TestData.validPassword());
    }

    @When("user logs in to mobile with email {string} and password {string}")
    public void userLogsInToMobileWithEmailAndPassword(String email, String password) {
        logger.info("Attempting mobile login with email: '{}'", email);
        loginPage().doLogin(email, password);
    }

    @When("user enters mobile email {string} and password {string} without submitting")
    public void userEntersMobileEmailWithoutSubmitting(String email, String password) {
        logger.info("Entering mobile email '{}' to trigger browser validation", email);
        loginPage().enterEmail(email).enterPassword(password).clickLogin();
    }

    // =========================================================================
    // THEN
    // =========================================================================

    @Then("mobile user should be redirected to the dashboard")
    public void mobileUserRedirectedToDashboard() {
        Assert.assertTrue(loginPage().isLoginSuccessful(),
            "Expected redirect to mobile dashboard. Check dashboard.url in config-mobile.properties");
    }

    @Then("mobile error message should be displayed")
    public void mobileErrorMessageDisplayed() {
        Assert.assertTrue(loginPage().isErrorDisplayed(),
            "Expected MUI error alert on mobile login page but it was not found");
    }

    @Then("mobile error message should contain {string}")
    public void mobileErrorMessageContains(String expectedError) {
        String actualError = loginPage().getErrorMessage();
        Assert.assertTrue(actualError.contains(expectedError),
            String.format("Expected mobile error to contain '%s' but got: '%s'", expectedError, actualError));
    }

    @Then("mobile browser validation should contain {string}")
    public void mobileBrowserValidationContains(String expected) {
        String actual = loginPage().getEmailValidationMessage();
        Assert.assertTrue(actual.contains(expected),
            String.format("Expected mobile browser validation to contain '%s' but got: '%s'", expected, actual));
    }

    @Then("mobile user should remain on the login page")
    public void mobileUserRemainsOnLoginPage() {
        Assert.assertTrue(loginPage().isLoginPageDisplayed(),
            "Mobile user should still be on the login page after a failed attempt");
    }
}
