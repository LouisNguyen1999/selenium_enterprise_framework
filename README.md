# Selenium Enterprise Framework

A lightweight enterprise-style Selenium TestNG framework with Page Object Model, environment-based configuration, reusable driver setup, report/screenshot helpers, and a smoke suite targeting Sauce Demo.

## Tech stack

- Java 17
- Maven
- Selenium WebDriver 4
- TestNG
- WebDriverManager
- Log4j2
- Jackson
- ExtentReports
- Apache POI

## Run tests

Run with the default environment:

```bash
mvn test
```

Run with a specific environment:

```bash
mvn test -Denv=qa
mvn test -Denv=staging
mvn test -Denv=prod
```

`ConfigLoader` reads `System.getProperty("env", Constants.DEFAULT_ENV)`. If `-Denv` is not provided, the framework uses `qa` by default.

## Environment configuration

Environment files live under:

- `src/main/resources/config`
- `src/test/resources/config`

Each environment file contains:

```properties
browser=chrome
headless=false
base.url=https://www.saucedemo.com/
```

During `mvn test`, test resources are copied to the test classpath, so files in `src/test/resources/config` are available for the test run. Use `-Denv=<name>` to select `qa.properties`, `staging.properties`, or `prod.properties`.

## Project structure

```text
selenium_enterprise_framework/
├── pom.xml
├── README.md
├── Jenkinsfile
├── docker-compose.yml
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── api/
    │   │   ├── components/
    │   │   ├── config/
    │   │   ├── data/
    │   │   ├── database/
    │   │   ├── driver/
    │   │   ├── enums/
    │   │   ├── listeners/
    │   │   ├── models/
    │   │   ├── pages/
    │   │   ├── report/
    │   │   └── utils/
    │   └── resources/
    │       ├── config/
    │       ├── files/
    │       ├── sql/
    │       └── testdata/
    └── test/
        ├── java/
        │   ├── base/
        │   ├── dataprovider/
        │   ├── hooks/
        │   └── tests/
        └── resources/
            ├── config/
            ├── sql/
            ├── testdata/
            └── testng.xml
```

## Root files

- `pom.xml`: Maven build file. Defines Java version, Selenium/TestNG/WebDriverManager dependencies, and Surefire configuration.
- `README.md`: Project documentation.
- `Jenkinsfile`: CI pipeline definition for Jenkins.
- `docker-compose.yml`: Docker Compose entry point for containerized/local service setup when needed.

## Main Java packages

- `src/main/java/api`: API helper layer. Currently contains placeholder-style helpers such as `LoginAPI` and `ProductAPI` for API setup or future API-backed test support.
- `src/main/java/components`: Reusable UI components that appear across pages, such as header, footer, and product cards.
- `src/main/java/config`: Framework configuration layer.
  - `ConfigLoader`: Loads `config/<env>.properties` from the classpath.
  - `Constants`: Stores config keys and default environment.
  - `DriverFactory`: Creates and stores driver managers in `ThreadLocal`.
  - `BrowserFactory`: Central browser creation helper for Chrome, Firefox, and Edge.
  - `Environment`: Enum for supported environment names.
- `src/main/java/data`: Test-data loading abstraction. `TestDataLoader` reads JSON data from `testdata/*.json`.
- `src/main/java/database`: Database helper layer. Contains simple DB connection/repository placeholders and SQL support entry points.
- `src/main/java/driver`: WebDriver lifecycle implementations.
  - `DriverManager`: Base lazy driver manager.
  - `LocalDriverManager`: Reads config and delegates local browser creation to `BrowserFactory`.
  - `RemoteDriverManager`: Intended for Selenium Grid/remote execution.
- `src/main/java/enums`: Shared enums such as browser type and user role. `BrowserType` validates configured browser values before driver creation.
- `src/main/java/listeners`: TestNG listeners and annotation transformers.
  - `TestListener`: Creates ExtentReports test nodes, logs pass/fail/skip callbacks, captures screenshots/page source on failure, and flushes the report.
  - `RetryListener`: Attaches retry behavior only to real test methods.
- `src/main/java/models`: POJO/domain objects such as `User` and `Product`.
- `src/main/java/pages`: Page Object Model layer.
  - `BasePage`: Shared wait/click/type/display helpers.
  - `LoginPage`: Login screen actions.
  - `HomePage`: Home/inventory landing behavior.
  - `ProductPage`: Product list and filtering actions.
  - `CartPage`: Cart page and checkout navigation.
  - `CheckoutPage`: Checkout form and completion flow.
- `src/main/java/report`: ExtentReports setup and per-test report management.
- `src/main/java/utils`: Shared utilities.
  - `JsonUtils`: Reads JSON resources.
  - `ExcelUtils`: Excel file helper using Apache POI.
  - `Logger`: Logging wrapper.
  - `PopupHandler`: Handles browser/app popups that may block tests.
  - `RandomDataUtils`: Test data generation helper.
  - `RetryAnalyzer`: TestNG retry analyzer.
  - `ScreenshotUtils`: Captures screenshots on failures.
  - `WaitUtils`: Additional wait helpers.

## Main resources

- `src/main/resources/config`: Default runtime environment configs.
- `src/main/resources/testdata`: JSON test data bundled with main resources.
- `src/main/resources/sql`: SQL scripts such as create/delete user scripts.
- `src/main/resources/files`: Static files used by tests, for example upload fixtures.
- `src/main/resources/log4j2.xml`: Log4j2 logging configuration.

## Test Java packages

- `src/test/java/base`: Test base setup/teardown.
  - `BaseTest`: Starts WebDriver, opens the app, exposes the active driver to listeners, and quits the driver.
- `src/test/java/dataprovider`: TestNG data providers.
  - `UserDataProvider`: Supplies login/user test data.
- `src/test/java/hooks`: Extra before/after hooks for test execution messages or shared setup.
- `src/test/java/tests/login`: Login test cases.
- `src/test/java/tests/product`: Product search/filter test cases.
- `src/test/java/tests/cart`: Cart test cases.
- `src/test/java/tests/checkout`: Checkout end-to-end test cases.

## Test resources

- `src/test/resources/testng.xml`: TestNG suite file used by Maven Surefire.
- `src/test/resources/config`: Test-run environment configs. These are loaded from classpath during `mvn test`.
- `src/test/resources/testdata`: JSON test data used by tests.
- `src/test/resources/sql`: SQL scripts available during test execution.
- `src/test/resources/files`: Test fixture files.

## Test flow

1. Maven Surefire runs `src/test/resources/testng.xml`.
2. TestNG loads listeners from the suite file.
3. `BaseTest.setUp()` initializes WebDriver through `DriverFactory`.
4. `ConfigLoader` loads the selected environment config.
5. Page objects execute UI actions against Sauce Demo.
6. On failure, `TestListener.onTestFailure()` captures screenshot/page source under `screenshots` and attaches the screenshot to ExtentReports.
7. `BaseTest.tearDown()` quits the browser.

## Notes

- Default environment is `qa`.
- Default browser comes from the selected properties file.
- Use `headless=true` in the selected config file for headless browser execution.
- If a stale compiled class causes strange TestNG errors, run:

```bash
mvn clean test
```
