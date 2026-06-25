# Dikahadir Automation

---

## Tech Stack

| Technology      | Version         | Purpose                                 |
|----------------|-----------------|------------------------------------------|
| Java            | 21              | Programming language                    |
| Maven           | 3.9+            | Build tool & dependency management      |
| Selenium        | 4.44.0          | Browser automation                      |
| Cucumber        | 7.34.3          | BDD framework & Gherkin syntax          |
| TestNG          | 7.12.0          | Test runner & assertions                |
| PicoContainer   | 7.34.3          | Dependency injection between steps      |
| WebDriverManager| 5.7.0           | Automatic driver binary management      |
| SLF4J + Logback | 2.0.18 / 1.5.34 | Logging framework                      |

---

## Project Structure

```
selenium-cucumber-boilerplate/
├── pom.xml
├── README.md
├── CONTRIBUTING.md
└── src/test/
    ├── java/com/kelompok1/cucumber/
    │   ├── core/
    │   │   ├── ConfigReader.java          # 3-tier config loader (sysprop > env > file)
    │   │   ├── DriverManager.java         # ThreadLocal WebDriver manager
    │   │   └── TestData.java              # Loads values from test-data.properties
    │   ├── exceptions/
    │   │   ├── ConfigurationException.java
    │   │   └── PageElementException.java
    │   ├── hooks/
    │   │   └── Hooks.java                 # @Before/@After WebDriver lifecycle
    │   ├── pages/
    │   │   ├── BasePage.java              # Reusable Selenium wrappers
    │   │   └── LoginPage.java             # Page Object: Login
    │   ├── runners/
    │   │   └── TestRunner.java            # TestNG Cucumber entry point
    │   └── stepdefinitions/
    │       └── LoginSteps.java            # Gherkin step implementations
    └── resources/
        ├── config.properties              # Runtime / environment config
        ├── cucumber.properties            # cucumber.publish.enabled only
        ├── logback.xml                    # Logging with rolling file appender
        ├── test-data.properties           # Single source of truth for test data
        └── features/
            └── login.feature              # Gherkin: Login scenarios
```

---

## Prerequisites

- **Java JDK** 21 or higher
- **Maven** 3.9 or higher
- **Chrome**, **Firefox**, or **Edge** installed

```bash
java -version
mvn -version
```

---

## Quick Start

### 1. Clone

```bash
git clone <repository-url>
cd selenium-cucumber-boilerplate
```

### 2. Run all tests

```bash
mvn clean test
```

### 3. Run with a specific browser

```bash
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
```

### 4. Run in headed mode (visible browser)

```bash
mvn clean test -Dheadless=false
```

### 5. Run by tag

```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
mvn clean test -Dcucumber.filter.tags="@positive"
mvn clean test -Dcucumber.filter.tags="@negative"
```

---

## Configuration

### config.properties

Controls runtime behavior — safe to commit.

```properties
base.url=https://magang.dikahadir.com/
browser=chrome
headless=true
implicit.wait=0
standard.wait=15
short.wait=5
page.load.timeout=30
browser.maximize=true
```

### Override priority

Values are resolved in this order (first match wins):

```
1. JVM system property   → -Dbase.url=https://staging.example.com
2. Environment variable  → BASE_URL=https://staging.example.com
3. config.properties     → fallback default
```

This means CI pipelines can inject any value without touching source files:

```bash
# GitHub Actions / Jenkins
# example
BASE_URL=https://staging.dikahadir.com BROWSER=firefox mvn clean test
```

---

## Test Data

All test data lives exclusively in `test-data.properties`. The `TestData` class loads from that file — there are no duplicate constants in Java. Change a value in one place only.

---

## Reporting

| Report        | Location                                  |
|---------------|-------------------------------------------|
| HTML          | `target/cucumber-reports/cucumber.html`   |
| JSON          | `target/cucumber-reports/cucumber.json`   |
| JUnit XML     | `target/cucumber-reports/cucumber.xml`    |
| Timeline      | `target/cucumber-reports/timeline/`       |
| Log file      | `target/logs/test.log`                    |

```bash
# Open HTML report
open target/cucumber-reports/cucumber.html        # macOS
xdg-open target/cucumber-reports/cucumber.html   # Linux
start target/cucumber-reports/cucumber.html       # Windows
```

---

## Extending the Boilerplate

To add a new feature (e.g. Inventory):

1. **Page Object** → `src/test/java/.../pages/InventoryPage.java`
2. **Step Definitions** → `src/test/java/.../stepdefinitions/InventorySteps.java`
3. **Feature file** → `src/test/resources/features/inventory.feature`
4. **Test data** → add keys to `test-data.properties`

---

## Design Decisions

| Decision | Rationale |
|---|---|
| `return this` in page methods | Fluent API — steps read like sentences |
| `ThreadLocal` WebDriver | Safe parallel execution — no shared state |
| Lazy page object creation | Prevents NPE from BasePage reading driver before `@Before` fires |
| 3-tier config loading | CI can inject values without touching files |
| Single source of truth for test data | Prevents test-data.properties vs TestData.java drift |
| No raw passwords in `.feature` files | Gherkin is shared widely; secrets don't belong there |
| `append=false` + rolling log | Each run gets a clean log; history is archived, not accumulated |
| `not @wip` in runner | In-progress scenarios excluded from CI automatically |

---

## Troubleshooting

### Driver version mismatch
WebDriverManager handles this automatically. If issues persist:
```bash
mvn clean test -Dwdm.clearDriverCache=true
```

### Tests pass locally but fail in CI headless
Increase timeout in config or override via env var:
```bash
STANDARD_WAIT=25 mvn clean test
```

### NPE in BasePage constructor
Page objects must be created inside step methods, not in the step definition constructor. See `LoginSteps.loginPage()` for the correct lazy initialization pattern.

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for Git workflow, branch naming, and commit message conventions.
