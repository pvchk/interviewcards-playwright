package com.interviewcards;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

/**
 * Base test class for Playwright tests.
 * Handles browser setup and teardown with better error handling.
 */
public abstract class BaseTest {
    
    // Shared Playwright instance
    static Playwright playwright;
    static Browser browser;
    
    // Test instance variables
    BrowserContext context;
    Page page;
    
    @BeforeAll
    static void launchBrowser() {
        try {
            playwright = Playwright.create();
            
            // Try to launch browser with error handling
            // Check system property for headless mode, default to false (headed mode)
            String headlessProperty = System.getProperty("headless", "false");
            boolean headless = Boolean.parseBoolean(headlessProperty);
            
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                    .setHeadless(headless);
            
            browser = playwright.chromium().launch(options);
            
        } catch (PlaywrightException e) {
            String errorMessage = e.getMessage();
            
            if (errorMessage != null && errorMessage.contains("Executable doesn't exist")) {
                System.err.println("\n❌ ERROR: Playwright browsers are not installed!");
                System.err.println("\nPlease install browsers before running tests:");
                System.err.println("  mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args=\"install chromium\"");
                System.err.println("\nOr use the installation script:");
                System.err.println("  ./install-browsers.sh\n");
            } else if (errorMessage != null && (errorMessage.contains("timed out") || errorMessage.contains("Download"))) {
                System.err.println("\n❌ ERROR: Browser download timed out or failed!");
                System.err.println("\nPlease install browsers manually:");
                System.err.println("  mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args=\"install chromium\"");
                System.err.println("\nOr check your internet connection and try again.\n");
            }
            
            throw new AssertionError("Failed to launch browser. See error messages above for installation instructions.", e);
        }
    }
    
    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    @BeforeEach
    void createContextAndPage() {
        if (browser == null) {
            throw new IllegalStateException("Browser not initialized. Check @BeforeAll setup.");
        }
        context = browser.newContext();
        page = context.newPage();
    }
    
    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }
    
    /**
     * Override this method to change headless mode for specific tests
     * @return true to run in headless mode, false to show browser
     */
    protected boolean isHeadless() {
        // Check system property, default to false (headed mode)
        String headless = System.getProperty("headless", "false");
        return Boolean.parseBoolean(headless);
    }
    
    /**
     * Get the current page instance
     * @return the Playwright Page instance
     */
    protected Page getPage() {
        return page;
    }
}

