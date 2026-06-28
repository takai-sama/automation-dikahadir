package com.kelompok1.cucumber.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

import com.kelompok1.cucumber.core.Platform;
import com.kelompok1.cucumber.core.PlatformContext;
import org.testng.annotations.AfterClass;
import com.kelompok1.cucumber.reporting.MDReportGenerator;

/**
 * TestNG runner for the Web Admin Panel.
 *
 * Platform is set in a static initializer so it is available before
 * AbstractTestNGCucumberTests initializes the Cucumber runtime.
 * 
 * @BeforeClass fires too late — after the runtime is already built.
 *
 *              Run this runner only:
 *              mvn clean test -Dtest=WebTestRunner
 *
 *              Run with tag filter:
 *              mvn clean test -Dtest=WebTestRunner
 *              -Dcucumber.filter.tags="@smoke"
 */
@CucumberOptions(features = "src/test/resources/features/web", glue = {
        "com.kelompok1.cucumber.stepdefinitions.web",
        "com.kelompok1.cucumber.hooks"
}, plugin = {
        "pretty",
        "html:target/cucumber-reports/web/cucumber.html",
        "json:target/cucumber-reports/web/cucumber.json",
        "junit:target/cucumber-reports/web/cucumber.xml",
        "timeline:target/cucumber-reports/web/timeline"
}, monochrome = true, tags = "@test")

public class WebTestRunner extends AbstractTestNGCucumberTests {

    static {
        PlatformContext.set(Platform.WEB);
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    @AfterClass(alwaysRun = true)
    @Override
    public void tearDownClass() {
        super.tearDownClass();
        MDReportGenerator.generate("web");
    }
}
