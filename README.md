# Java Playwright Test Automation Project

A Java-based test automation project using Playwright for browser automation.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Setup

### 1. Install Dependencies

First, install Maven dependencies:

```bash
mvn clean install
```

### 2. Install Playwright Browsers

After installing dependencies, install the Playwright browsers. Try these methods in order:

**Method 1: Using Maven Exec Plugin (Recommended)**
```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

**Method 2: Using Maven Exec Plugin (Alternative)**
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

**Method 3: Using Node.js Playwright CLI (if Node.js is installed)**
```bash
npx playwright install
```

**Method 4: Install specific browser only**
```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

**Check Browser Installation:**

Before running tests, check if browsers are installed:

```bash
./check-browsers.sh
```

**Troubleshooting Browser Download Issues:**

If you encounter errors when downloading or using browsers:

1. **Timeout Error (Request timed out after 30000ms):**
   - The browser download is timing out. You need to install browsers BEFORE running tests
   - Run: `mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"`
   - Or use: `./install-browsers.sh`

2. **400 Error (server returned code 400):**
   - CDN issue or outdated Playwright version
   - Clear Maven cache: `rm -rf ~/.m2/repository/com/microsoft/playwright`
   - Reinstall: `mvn clean install` then install browsers again

3. **Executable doesn't exist Error:**
   - Browsers are not installed. Install them using the methods above.

4. **General Troubleshooting:**
   - Ensure you have a stable internet connection
   - Check firewall/proxy settings that might block downloads
   - Try again later if CDN is temporarily unavailable
   - Use the installation script which includes retry logic: `./install-browsers.sh`

### 3. Run Tests

Run all tests:

```bash
mvn test
```

Run a specific test class:

```bash
mvn test -Dtest=ExampleTest
```

### 4. Run Tests with Options

Run tests in headed mode (see the browser):

The example test already runs in headed mode (`setHeadless(false)`). To run in headless mode, modify the test or add a system property.

## Project Structure

```
.
├── pom.xml                                    # Maven configuration
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── interviewcards/           # Main source code
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── interviewcards/           # Test classes
│       └── resources/                         # Test resources (configs, etc.)
└── README.md
```

## Dependencies

- **Playwright**: 1.48.0 - Browser automation framework
- **JUnit 5**: 5.10.2 - Testing framework

## Configuration

### Browser Selection

By default, the tests use Chromium. To use other browsers, modify the test setup:

```java
// Firefox
browser = playwright.firefox().launch(options);

// WebKit
browser = playwright.webkit().launch(options);
```

### Headless Mode

To run tests in headless mode, change:

```java
browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
```

## Writing Tests

The project includes a `BaseTest` class that handles browser setup and teardown automatically.

### Using BaseTest (Recommended)

1. Create a new test class in `src/test/java/com/interviewcards/`
2. Extend `BaseTest` class
3. Use `getPage()` method to access the Playwright Page instance
4. The browser will be automatically set up and torn down

Example:

```java
public class MyTest extends BaseTest {
    @Test
    void myTest() {
        getPage().navigate("https://example.com");
        getPage().click("button");
        String text = getPage().locator(".result").textContent();
        assertEquals("Expected text", text);
    }
}
```

### Benefits of BaseTest

- **Automatic browser management**: Browser is launched once and shared across tests
- **Better error messages**: Clear instructions if browsers are not installed
- **Error handling**: Handles timeout and download errors gracefully
- **Configurable**: Use system property `-Dheadless=true` to run in headless mode

### Running Tests in Headless Mode

```bash
mvn test -Dheadless=true
```

## Resources

- [Playwright Java Documentation](https://playwright.dev/java/)
- [Playwright API Reference](https://playwright.dev/java/docs/api/class-playwright)

