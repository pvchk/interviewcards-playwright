# Java Playwright Test Automation Project

A Java-based test automation project using Playwright for browser automation with Allure reporting.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Setup

### 1. Install Dependencies

```bash
mvn clean install
```

### 2. Install Playwright Browsers

Install Playwright browsers using one of these methods:

**Method 1: Using Maven Exec Plugin (Recommended)**
```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

**Method 2: Using Node.js Playwright CLI (if Node.js is installed)**
```bash
npx playwright install chromium
```

**Troubleshooting Browser Installation:**

- **Timeout Error**: Ensure stable internet connection and try again
- **400 Error**: Clear Maven cache: `rm -rf ~/.m2/repository/com/microsoft/playwright` then reinstall
- **Executable doesn't exist**: Browsers are not installed. Use one of the installation methods above

### 3. Configure Environment Variables

Tests require `TEST_USERNAME` and `TEST_PASSWORD` environment variables. You can set them in two ways:

**Option 1: Export in current session**
```bash
export TEST_USERNAME="test1"
export TEST_PASSWORD="test123"
export TEST_EMAIL="test1@test1.test1"
```

**Option 2: Use system properties (no export needed)**
```bash
mvn test -DTEST_USERNAME="test1" -DTEST_PASSWORD="test123" -DTEST_EMAIL="test1@test1.test1"
```

**Option 3: Add to `~/.zshrc` for permanent setup**
```bash
echo 'export TEST_USERNAME="test1"' >> ~/.zshrc
echo 'export TEST_PASSWORD="test123"' >> ~/.zshrc
echo 'export TEST_EMAIL="test1@test1.test1"' >> ~/.zshrc
source ~/.zshrc
```

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=LoginTest
```

### Clean and Run
```bash
mvn clean test
```

## Allure Reporting

This project uses Allure for test reporting with automatic screenshot capture on failures.

### Generate and View Reports

**Generate report:**
```bash
mvn allure:report
```

**Serve report (opens in browser automatically):**
```bash
mvn allure:serve
```

The report includes:
- Test execution results with pass/fail status
- **Automatic screenshots** captured on test failures
- Test steps, descriptions, and metadata
- Test history and trends
- Interactive HTML interface

### Features

- **Automatic Screenshot Capture**: Screenshots are automatically captured and attached when tests fail
- **Test Annotations**: Tests use `@Epic`, `@Feature`, `@Story`, `@Severity` for better organization
- **Default Timeout**: Tests use 5-second timeout for actions and navigation (configurable in `BaseTest`)

## Project Structure

```
.
├── pom.xml                                    # Maven configuration
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── config/                        # Configuration (credentials, URLs)
│   │       ├── pages/                         # Page Object Model classes
│   │       └── components/                    # Reusable UI components
│   └── test/
│       ├── java/
│       │   └── com/interviewcards/           # Base test class
│       │   └── tests/                         # Test classes
│       └── resources/                         # Test resources
└── README.md
```

## Resources

- [Playwright Java Documentation](https://playwright.dev/java/)
- [Playwright API Reference](https://playwright.dev/java/docs/api/class-playwright)
- [Allure Framework](https://docs.qameta.io/allure/)
