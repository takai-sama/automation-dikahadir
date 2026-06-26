package com.kelompok1.cucumber.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

import com.kelompok1.cucumber.core.Platform;
import com.kelompok1.cucumber.core.PlatformContext;
import org.testng.annotations.AfterClass;
import com.kelompok1.cucumber.reporting.MDReportGenerator;

/**
 * TestNG runner for the Mobile Mirroring Panel.
 *
 * Platform is set in a static initializer so it is available before
 * AbstractTestNGCucumberTests initializes the Cucumber runtime.
 * @BeforeClass fires too late — after the runtime is already built.
 *
 * Run this runner only:
 *   mvn clean test -Dtest=MobileTestRunner
 *
 * Run with tag filter:
 *   mvn clean test -Dtest=MobileTestRunner -Dcucumber.filter.tags="@smoke"
 */
@CucumberOptions(
    features = "src/test/resources/features/mobile",
    glue = {
        "com.kelompok1.cucumber.stepdefinitions.mobile",
        "com.kelompok1.cucumber.hooks"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/mobile/cucumber.html",
        "json:target/cucumber-reports/mobile/cucumber.json",
        "junit:target/cucumber-reports/mobile/cucumber.xml",
        "timeline:target/cucumber-reports/mobile/timeline"
    },
    monochrome = true,
    tags = "not @wip"
)
public class MobileTestRunner extends AbstractTestNGCucumberTests {

    static {
        PlatformContext.set(Platform.MOBILE);
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    @AfterClass(alwaysRun = true)
    @Override
    public void tearDownClass() {
        super.tearDownClass(); // ← flushes cucumber.json to disk
        MDReportGenerator.generate("mobile");
    }
}
