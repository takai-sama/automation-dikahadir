package com.kelompok1.cucumber.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kelompok1.cucumber.core.ConfigReader;
import com.kelompok1.cucumber.core.DriverManager;
import com.kelompok1.cucumber.core.PlatformContext;

import java.time.Duration;

/**
 * Cucumber Hooks — WebDriver lifecycle management.
 *
 * @Before  creates and configures the WebDriver for the active platform.
 * @After   captures screenshot on failure, then quits the driver.
 *
 * ConfigReader reads from the platform-specific config file, so browser,
 * timeouts, and headless mode can differ between web and mobile runs if needed.
 */
public class Hooks {

    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);

    @Before(order = 0)
    public void setUp() {
        logger.info("Setting up WebDriver for platform: {}", PlatformContext.get());

        WebDriver driver = createWebDriver();
        driver.manage().timeouts().implicitlyWait(
            Duration.ofSeconds(ConfigReader.getInt("implicit.wait")));
        driver.manage().timeouts().pageLoadTimeout(
            Duration.ofSeconds(ConfigReader.getInt("page.load.timeout", 30)));

        if (ConfigReader.getBoolean("browser.maximize", true)) {
            driver.manage().window().maximize();
        }

        DriverManager.setDriver(driver);
        logger.info("WebDriver initialized: {}", driver.getClass().getSimpleName());
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            logger.error("Scenario FAILED: {}", scenario.getName());
            try {
                final byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failure Screenshot");
                logger.info("Screenshot attached for failed scenario");
            } catch (Exception e) {
                logger.error("Failed to capture screenshot: {}", e.getMessage());
            }
        }

        logger.info("Quitting WebDriver for scenario: {}", scenario.getName());
        DriverManager.quit();
    }

    private WebDriver createWebDriver() {
        String browser  = ConfigReader.getProperty("browser", "chrome").toLowerCase().trim();
        boolean headless = ConfigReader.getBoolean("headless");

        logger.info("Creating {} WebDriver (headless={})", browser, headless);

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                return new ChromeDriver(chromeOptions);

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) firefoxOptions.addArguments("--headless");
                return new FirefoxDriver(firefoxOptions);

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.addArguments("--headless=new");
                edgeOptions.addArguments("--no-sandbox");
                edgeOptions.addArguments("--disable-dev-shm-usage");
                return new EdgeDriver(edgeOptions);

            default:
                throw new IllegalArgumentException(
                    "Unsupported browser: '" + browser + "'. Valid: chrome, firefox, edge");
        }
    }
}
